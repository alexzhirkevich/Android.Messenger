package com.alexz.messenger.app.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import android.widget.*
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.messenger.app.R

typealias OnClickListener = (di : DialogInterface,witch : Int, checked: Boolean?) -> Unit

class CustomDialogBuilder(context: Context, @StyleRes theme : Int = R.style.DialogTheme/*resolveDialogTheme(context,0)*/)
    : AlertDialog.Builder(context,theme){

    private val view = DialogView(context)

    fun setCheckBox(@StringRes text: Int): CustomDialogBuilder =
            setCheckBox(context.getString(text))

    fun setCheckBox(text : String?) : CustomDialogBuilder{
        if (text != null){
            view.checkBox.isVisible = true
            view.checkBox.text = text
        } else {
            view.checkBox.text = ""
            view.checkBox.isVisible = false
        }
        return this
    }

    fun setPositiveButton(@StringRes textId: Int, listener: OnClickListener?): CustomDialogBuilder =
        setPositiveButton(context.getString(textId),listener)

    fun setPositiveButton(text: CharSequence?, listener: OnClickListener?): CustomDialogBuilder =
        super.setPositiveButton(text) { di, w ->
            listener?.invoke(di, w, view.checkBox.takeIf { it.isVisible }?.isChecked)
        } as CustomDialogBuilder


    fun setNegativeButton(@StringRes textId: Int, listener: OnClickListener?): CustomDialogBuilder =
            setNegativeButton(context.getString(textId),listener)

    fun setNegativeButton(text: CharSequence?, listener: OnClickListener?): CustomDialogBuilder =
        super.setNegativeButton(text) { di, w ->
            listener?.invoke(di, w, view.checkBox.takeIf { it.isVisible }?.isChecked)
        } as CustomDialogBuilder


    fun setNeutralButton(@StringRes textId: Int, listener: OnClickListener?): CustomDialogBuilder =
        setNeutralButton(context.getString(textId),listener)


    fun setNeutralButton(text: CharSequence?, listener: OnClickListener?): CustomDialogBuilder =
        super.setNeutralButton(text) { di, w ->
            listener?.invoke(di, w, view.checkBox.takeIf { it.isVisible }?.isChecked)
        } as CustomDialogBuilder

    override fun setIcon(icon: Drawable?): CustomDialogBuilder {
        view.icon.setImageDrawable(icon)
        return this
    }

    override fun setIcon(iconId: Int): CustomDialogBuilder {
        view.icon.setImageResource(iconId)
        return this
    }

    override fun setTitle(titleId: Int): CustomDialogBuilder {
        view.title.text = context.getString(titleId)
        return this
    }

    override fun setTitle(title: CharSequence?): CustomDialogBuilder {
        view.title.text = title
        return this
    }

    override fun setMessage(messageId: Int): CustomDialogBuilder {
        view.message.text = context.getString(messageId)
        return this
    }

    override fun setMessage(message: CharSequence?): CustomDialogBuilder {
        view.message.text = message
        return this
    }

    override fun show(): AlertDialog {
        setView(view)
        return super.show().apply {
//            window?.decorView?.setBackgroundResource(R.drawable.dialog_bg)
        }
    }


    @Deprecated("Use listener from CustomDialogBuilder", ReplaceWith("Same method with CustomDialogBuilder OnClickListener"))
    override fun setPositiveButton(textId: Int, listener: DialogInterface.OnClickListener?): CustomDialogBuilder {
        return super.setPositiveButton(textId, listener) as CustomDialogBuilder
    }

    @Deprecated("Use listener from CustomDialogBuilder", ReplaceWith("Same method with CustomDialogBuilder OnClickListener"))
    override fun setPositiveButton(text: CharSequence?, listener: DialogInterface.OnClickListener?): CustomDialogBuilder {
        return super.setPositiveButton(text, listener) as CustomDialogBuilder
    }

    @Deprecated("Use listener from CustomDialogBuilder", ReplaceWith("Same method with CustomDialogBuilder OnClickListener"))
    override fun setNegativeButton(textId: Int, listener: DialogInterface.OnClickListener?): CustomDialogBuilder {
        return super.setNegativeButton(textId, listener) as CustomDialogBuilder
    }

    @Deprecated("Use listener from CustomDialogBuilder", ReplaceWith("Same method with CustomDialogBuilder OnClickListener"))
    override fun setNegativeButton(text: CharSequence?, listener: DialogInterface.OnClickListener?): CustomDialogBuilder {
        return super.setNegativeButton(text, listener) as CustomDialogBuilder
    }

    @Deprecated("Use listener from CustomDialogBuilder", ReplaceWith("Same method with CustomDialogBuilder OnClickListener"))
    override fun setNeutralButton(textId: Int, listener: DialogInterface.OnClickListener?): CustomDialogBuilder {
        return super.setNeutralButton(textId, listener) as CustomDialogBuilder
    }

    @Deprecated("Use listener from CustomDialogBuilder", ReplaceWith("Same method with CustomDialogBuilder OnClickListener"))
    override fun setNeutralButton(text: CharSequence?, listener: DialogInterface.OnClickListener?): CustomDialogBuilder {
        return super.setNeutralButton(text, listener) as CustomDialogBuilder
    }

    override fun setCursor(cursor: Cursor?, listener: DialogInterface.OnClickListener?, labelColumn: String?): CustomDialogBuilder {
        return super.setCursor(cursor, listener, labelColumn) as CustomDialogBuilder
    }

    override fun setIconAttribute(attrId: Int): CustomDialogBuilder {
        return super.setIconAttribute(attrId) as CustomDialogBuilder
    }

    override fun setCustomTitle(customTitleView: View?): CustomDialogBuilder {
        return super.setCustomTitle(customTitleView) as CustomDialogBuilder
    }

    override fun setCancelable(cancelable: Boolean): CustomDialogBuilder {
        return super.setCancelable(cancelable) as CustomDialogBuilder
    }

    override fun setOnCancelListener(onCancelListener: DialogInterface.OnCancelListener?): CustomDialogBuilder {
        return super.setOnCancelListener(onCancelListener) as CustomDialogBuilder
    }

    override fun setItems(itemsId: Int, listener: DialogInterface.OnClickListener?):CustomDialogBuilder {
        return super.setItems(itemsId, listener) as CustomDialogBuilder
    }

    override fun setItems(items: Array<out CharSequence>?, listener: DialogInterface.OnClickListener?): CustomDialogBuilder {
        return super.setItems(items, listener) as CustomDialogBuilder
    }

    override fun create(): AlertDialog {
        setView(view)
        return super.create()
    }

    @SuppressLint("RestrictedApi")
    override fun setRecycleOnMeasureEnabled(enabled: Boolean): CustomDialogBuilder {
        return super.setRecycleOnMeasureEnabled(enabled) as CustomDialogBuilder
    }

    override fun setPositiveButtonIcon(icon: Drawable?): CustomDialogBuilder {
        return super.setPositiveButtonIcon(icon) as CustomDialogBuilder
    }

    override fun setNegativeButtonIcon(icon: Drawable?): CustomDialogBuilder {
        return super.setNegativeButtonIcon(icon) as CustomDialogBuilder
    }

    override fun setOnKeyListener(onKeyListener: DialogInterface.OnKeyListener?): CustomDialogBuilder {
        return super.setOnKeyListener(onKeyListener) as CustomDialogBuilder
    }

    override fun setView(layoutResId: Int): CustomDialogBuilder {
        return super.setView(layoutResId) as CustomDialogBuilder
    }

    override fun setView(view: View?): CustomDialogBuilder {
        return super.setView(view) as CustomDialogBuilder
    }

    @SuppressLint("RestrictedApi")
    override fun setView(view: View?, viewSpacingLeft: Int, viewSpacingTop: Int, viewSpacingRight: Int, viewSpacingBottom: Int): CustomDialogBuilder {
        return super.setView(view, viewSpacingLeft, viewSpacingTop, viewSpacingRight, viewSpacingBottom) as CustomDialogBuilder
    }

    override fun setInverseBackgroundForced(useInverseBackground: Boolean): CustomDialogBuilder {
        return super.setInverseBackgroundForced(useInverseBackground) as CustomDialogBuilder
    }

    override fun setAdapter(adapter: ListAdapter?, listener: DialogInterface.OnClickListener?): CustomDialogBuilder {
        return super.setAdapter(adapter, listener) as CustomDialogBuilder
    }

    override fun setNeutralButtonIcon(icon: Drawable?): CustomDialogBuilder {
        return super.setNeutralButtonIcon(icon) as CustomDialogBuilder
    }

    override fun setMultiChoiceItems(itemsId: Int, checkedItems: BooleanArray?, listener: DialogInterface.OnMultiChoiceClickListener?): CustomDialogBuilder {
        return super.setMultiChoiceItems(itemsId, checkedItems, listener) as CustomDialogBuilder
    }

    override fun setMultiChoiceItems(items: Array<out CharSequence>?, checkedItems: BooleanArray?, listener: DialogInterface.OnMultiChoiceClickListener?): CustomDialogBuilder {
        return super.setMultiChoiceItems(items, checkedItems, listener) as CustomDialogBuilder
    }

    override fun setMultiChoiceItems(cursor: Cursor?, isCheckedColumn: String?, labelColumn: String?, listener: DialogInterface.OnMultiChoiceClickListener?): CustomDialogBuilder {
        return super.setMultiChoiceItems(cursor, isCheckedColumn, labelColumn, listener) as CustomDialogBuilder
    }

    override fun setOnDismissListener(onDismissListener: DialogInterface.OnDismissListener?): CustomDialogBuilder {
        return super.setOnDismissListener(onDismissListener) as CustomDialogBuilder
    }

    override fun setSingleChoiceItems(itemsId: Int, checkedItem: Int, listener: DialogInterface.OnClickListener?): CustomDialogBuilder {
        return super.setSingleChoiceItems(itemsId, checkedItem, listener) as CustomDialogBuilder
    }

    override fun setSingleChoiceItems(cursor: Cursor?, checkedItem: Int, labelColumn: String?, listener: DialogInterface.OnClickListener?): CustomDialogBuilder {
        return super.setSingleChoiceItems(cursor, checkedItem, labelColumn, listener) as CustomDialogBuilder
    }

    override fun setSingleChoiceItems(items: Array<out CharSequence>?, checkedItem: Int, listener: DialogInterface.OnClickListener?): CustomDialogBuilder {
        return super.setSingleChoiceItems(items, checkedItem, listener) as CustomDialogBuilder
    }

    override fun setSingleChoiceItems(adapter: ListAdapter?, checkedItem: Int, listener: DialogInterface.OnClickListener?): CustomDialogBuilder {
        return super.setSingleChoiceItems(adapter, checkedItem, listener) as CustomDialogBuilder
    }

    override fun setOnItemSelectedListener(listener: AdapterView.OnItemSelectedListener?): CustomDialogBuilder {
        return super.setOnItemSelectedListener(listener) as CustomDialogBuilder
    }

    private companion object {
        private fun resolveDialogTheme(context: Context, @StyleRes resid: Int): Int {
            // Check to see if this resourceId has a valid package ID.
            return if (resid ushr 24 and 0x000000ff >= 0x00000001) {   // start of real resource IDs.
                resid
            } else {
                val outValue = TypedValue()
                context.theme.resolveAttribute(R.attr.alertDialogTheme, outValue, true)
                outValue.resourceId
            }
        }
    }

    private class DialogView(context: Context) : RelativeLayout(context) {
        init {
            inflate(context, R.layout.custom_dialog_view,this)
        }

        val title = findViewById<TextView>(R.id.dialog_title)
        val icon = findViewById<ImageView>(R.id.dialog_icon)
        val message = findViewById<TextView>(R.id.dialog_message)
        val checkBox = findViewById<CheckBox>(R.id.dialog_check_box)

    }
}