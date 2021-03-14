package com.alexz.messenger.app.ui.common.firerecyclerview;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.alexz.messenger.app.data.model.interfaces.IBaseModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class FirebaseMapRecyclerAdapter<Model extends IBaseModel, VH extends FirebaseViewHolder<Model>>
        extends FirebaseDefaultRecyclerAdapter<Model,VH>
        implements IFirebaseMapRecyclerAdapter<Model,VH>, Listenable {

    private static final String TAG = FirebaseMapRecyclerAdapter.class.getSimpleName();

    private Query keysQuery;

    private final Map<String, ObservableModelInfo> modelsInfo = new ConcurrentHashMap<>();

    private ChildEventListener keysQueryListener;

    private boolean listening = false;
    private boolean loading = false;
    private long chatsCount = -1;




    /**
     * Model class for {@link DataSnapshot} parsing
     * @param modelClass - class of the adapter Model
     * @see IBaseModel
     */
    public FirebaseMapRecyclerAdapter(Class<Model> modelClass){
        super(modelClass);
    }



    /**
     * @see IFirebaseRecyclerAdapter#parse(DataSnapshot)
     * */
    @Override
    public Model parse(DataSnapshot snapshot) {
        return snapshot.getValue(modelClass);
    }



    /**
     * @see IFirebaseRecyclerAdapter#onSelect(String, IBaseModel)
     * */
    @Override
    public boolean onSelect(@Nullable String selectionKey, @NonNull Model model) {
        return true;
    }

    
    
    /**
     * @see IFirebaseMapRecyclerAdapter#onModelNotFound(String)
     * */
    @Override
    public void onModelNotFound(@NonNull String modelId) { }



    /**
     * @see Listenable#startListening() ()
     */
    @Override
    public final void startListening() {
        if (!listening) {
            listening = true;
            if (keysQueryListener == null) {

                if (loadingCallback!=null) {
                    uiHandler.post(() -> loadingCallback.onStartLoading());
                }
                loading = true;

                keysQueryListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        observeModel(snapshot.getKey(),false);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (previousChildName != null) {
                            stopObserve(previousChildName);
                        }
                        observeModel(snapshot.getKey(),true);
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        stopObserve(snapshot.getKey());
                        chatsCount--;
                        checkLoadingEnd();
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        stopObserve(snapshot.getKey());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w(TAG, "Failed to get keys");
                    }
                };
                keysQuery = onCreateKeyQuery();
                keysQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            chatsCount = snapshot.getChildrenCount();
                            keysQuery.addChildEventListener(keysQueryListener);
                            Log.w(TAG, "Loading " + chatsCount + " chats");
                            if (chatsCount == 0 && loadingCallback != null){
                                uiHandler.post(() -> loadingCallback.onEndLoading());
                            }
                        } else{
                            if (loadingCallback != null){
                                uiHandler.post(() -> loadingCallback.onEndLoading());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w(TAG, "Failed to get keys count");
                        if (loadingCallback != null){
                            uiHandler.post(() -> loadingCallback.onEndLoading());
                        }
                    }
                });

            } else {
                keysQuery.addChildEventListener(keysQueryListener);
                for (String key : modelsInfo.keySet()) {
                    ObservableModelInfo info = modelsInfo.get(key);
                    if (info != null) {
                        info.startListening();
                    }
                }
                keysQuery.addChildEventListener(keysQueryListener);
            }
        }
    }



    /**
     * @see Listenable#stopListening()
     */
    @Override
    public final void stopListening() {
        if (listening) {
            listening = false;
            keysQuery.removeEventListener(keysQueryListener);
            for (String key : modelsInfo.keySet()){
                ObservableModelInfo info  = modelsInfo.get(key);
                if (info!=null) {
                    info.stopListening();
                }
            }
        }
    }



    @CallSuper
    @Override
    protected void finalize() throws Throwable {
        stopListening();
        super.finalize();
    }



    private void observeModel(String modelId,boolean changed) {
        if (modelsInfo.containsKey(modelId) && modelsInfo.get(modelId) != null) {
            Objects.requireNonNull(modelsInfo.get(modelId)).startListening();
        } else {
            ObservableModelInfo info = new ObservableModelInfo(modelId, new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {

                        Model newModel = parse(snapshot);
                        if (newModel != null) {
                            int idx = add(newModel);
                            if (!isSearching()) {
                                if (idx == realItemCount()){
                                    notifyItemInserted(realItemCount());
                                } else {
                                    notifyItemChanged(idx);
                                }
                            }
                            if (adapterCallback != null) {
                                if (changed) {
                                    uiHandler.post(() -> adapterCallback.onItemChanged(newModel));
                                } else {
                                    uiHandler.post(() -> adapterCallback.onItemAdded(newModel));
                                }
                            }
                            checkLoadingEnd();
                        }
                    } else {
                        onModelNotFound(modelId);
                        Log.w(TAG, "Failed to observe model. Id: " + modelId);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w(TAG, "Failed to observe model. Id: " + modelId);
                }
            });
            modelsInfo.put(modelId, info);
            if (listening) {
                info.startListening();
            }
        }
    }

    private void stopObserve(String modelId){
        ObservableModelInfo info = modelsInfo.remove(modelId);
        if (info != null) {
            info.stopListening();
            Pair<Model, Integer> removed = remove(modelId);
            if (removed != null) {
                if (adapterCallback != null) {
                    uiHandler.post(() -> adapterCallback.onItemRemoved(removed.first));
                }
                if (!isSearching()) {
                    notifyItemRemoved(removed.second);
                }
            }
        }
    }

    private void checkLoadingEnd(){
        if (loading && realItemCount() >= chatsCount){
            loading = false;
            if (loadingCallback != null) {
                uiHandler.post(() -> loadingCallback.onEndLoading());
            }
        }
    }

    @Override
    protected final int add(Model m) {
        return super.add(m);
    }

    @Nullable
    @Override
    protected final Pair<Model, Integer> remove(String id) {
        return super.remove(id);
    }

    @Override
    protected final void removeAll() {
        super.removeAll();
    }

    @Override
    protected final int realItemCount() {
        return super.realItemCount();
    }

    private class ObservableModelInfo implements Listenable {

        boolean listening = false;
        private final Query query;
        private final ValueEventListener listener;

        private ObservableModelInfo(String id, ValueEventListener listener){
            this.query = onCreateModelQuery(id);
            this.listener = listener;
        }

        @Override
        public void startListening(){
            if (!listening && listener != null) {
                listening = true;
                query.addValueEventListener(listener);
            }
        }

        @Override
        public void stopListening(){
            if (listening && listener != null) {
                listening = false;
                query.removeEventListener(listener);
            }
        }

        @Override
        protected void finalize() throws Throwable {
            stopListening();
            super.finalize();
        }
    }
}
