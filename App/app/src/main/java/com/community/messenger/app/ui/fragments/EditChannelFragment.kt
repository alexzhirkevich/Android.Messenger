package com.community.messenger.app.ui.fragments

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.community.messenger.app.ui.viewmodels.EditChannelViewModel
import com.community.messenger.common.entities.imp.Channel
import com.community.messenger.common.entities.interfaces.IChannel
import com.community.messenger.common.util.isLatinDigitOrUnderscore
import com.community.messenger.app.R
import com.community.messenger.app.databinding.FragmentEditChannelBinding
import kotlinx.android.synthetic.main.fragment_edit_channel.*
import kotlinx.coroutines.*

class EditChannelFragment
    : InputFragment<FragmentEditChannelBinding>() {

    companion object CREATOR {
        const val EXTRA_CHANNEL = "EXTRA_CHANNEL"

        fun newBundle(channel: IChannel): Bundle {
            return bundleOf(EXTRA_CHANNEL to channel)
        }
    }

    override val inputs: Collection<EditText>
        get() = listOf(binding.editName, binding.editTag, binding.editDescription)

    private var channel: IChannel? = arguments?.getParcelable(EXTRA_CHANNEL)

    private val viewModel: EditChannelViewModel by viewModels()
    init {
        if (channel != null)
            viewModel.parameter = channel!!.id
    }

    private var imageUri : String?= channel?.imageUri

    private val nameMinLen: Int by lazy { requireContext().resources.getInteger(R.integer.channel_name_len_min) }
    private val tagMinLen: Int by lazy { requireContext().resources.getInteger(R.integer.channel_tag_len_min) }
    private val fabAnimLen: Long by lazy { resources.getInteger(R.integer.anim_duration_short).toLong() }

    override val isFabCanBeEnabled: Boolean
        get() = with(binding) {
            editName.text?.length ?: 0 >= nameMinLen &&
                    editDescription.text?.count { it.toString() == System.lineSeparator() } ?: 0 <= 1 &&
                    editTag.text.toString().isLatinDigitOrUnderscore &&
                    (editTag.text?.isEmpty() == true || editTag.text?.length ?: 0 >= tagMinLen) &&
                    (viewModel.tagAvailable.value == true || editTag.text?.toString().orEmpty() == channel?.tag.orEmpty()) &&
                    !isBtnBlocked
        }

    private val tagObserver = Observer<Boolean> {
        isBtnBlocked = false
        fab.isEnabled = isFabCanBeEnabled
        with(binding) {
            if (it) {
                editTag.error = null
                layoutTag.boxStrokeColor = successColor
                layoutTag.setStartIconTintList(ColorStateList.valueOf(successColor))
            } else {
                editTag.error = getString(R.string.error_tag_taken)
                layoutTag.boxStrokeColor = errorColor
                with(layoutTag) { setStartIconTintList(ColorStateList.valueOf(errorColor)) }
            }
        }
    }

    private val getImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            requireActivity().lifecycleScope.launch(Dispatchers.Main) {
                try {
                    binding.btnAvatar.setImageURI(it)
                    imageUri = viewModel.uploadImage(it)
                    if (!this@EditChannelFragment.isAdded) {
                        viewModel.setImageUri(imageUri!!)
                    }
                } catch (t: Throwable) {

                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (channel != null){
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEditName()
        setupEditTag()
        setupEditDescription()
        setupBtnAvatar()

        viewModel.tagAvailable.observe(viewLifecycleOwner, tagObserver)
    }

    override fun onKeyboardVisibilityChanged(visible: Boolean) {
        fab.apply {
            if (visible) {
                (layoutParams as RelativeLayout.LayoutParams).apply {
                    removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                    addRule(RelativeLayout.ALIGN_TOP, R.id.toolbar)
                }
                requestLayout()
                scaleX = 0f
                scaleY = 0f
                animate().scaleX(1f).scaleY(1f).setDuration(fabAnimLen).start()
            } else {
                (layoutParams as RelativeLayout.LayoutParams).apply {
                    removeRule(RelativeLayout.ALIGN_TOP)
                    addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                }
                requestLayout()
            }
        }
    }

    @SuppressLint("CheckResult")
    override fun onBtnClicked(v: View) {

        val name = binding.editName.text.toString()
        val tag = binding.editTag.text.toString()
        val desc = binding.editDescription.text.toString()

        inputs.forEach{ it.isEnabled = false}
        v.isEnabled = false
        binding.btnAvatar.isClickable = false

        lifecycleScope.launch(Dispatchers.Main) {
            val success = if (channel == null) {
                val newChannel  = Channel(
                    name = name,
                    tag = tag,
                    imageUri = imageUri.orEmpty(),
                    description = desc,
                )
                channel = createChannel(newChannel)
                channel  != null
            } else {
                updateChannel(channel!!,name, tag, desc)
            }
            if (success) {
                exit()
                parentFragmentManager.replace(
                    R.id.fragment_host_main,
                    ChannelFragment(),
                    ChannelFragment.newBundle(channel!!)
                ) {
                    setCustomAnimations(
                        R.anim.anim_fragment_in, R.anim.anim_fragment_out,
                        R.anim.anim_fragment_in, R.anim.anim_fragment_out
                    )
                }
            } else{
                inputs.forEach{ it.isEnabled = true}
                v.isEnabled = true
                binding.btnAvatar.isClickable = true
            }
        }
    }

    override fun onDestroyView() {
        viewModel.tagAvailable.removeObservers(viewLifecycleOwner)
        super.onDestroyView()
    }

    private fun setupEditName() {
        with(binding) {
            binding.editName.apply {
                doOnTextChanged { _, _, _, _ ->
                    when {
                        text?.isNotEmpty() == true && text?.length ?: 0 < nameMinLen -> {
                            error = resources.getString(R.string.eror_min_len, nameMinLen)
                            layoutName.boxStrokeColor = errorColor
                            layoutName.setStartIconTintList(ColorStateList.valueOf(errorColor))

                        }
                        else -> {
                            error = null
                            layoutName.boxStrokeColor = successColor
                            layoutName.setStartIconTintList(ColorStateList.valueOf(successColor))
                        }
                    }
                }
                setText(channel?.name)
            }
        }
    }

    private fun setupEditTag() {
        binding.editTag.apply {
            doOnTextChanged { _, _, _, _ ->
                fab.isEnabled = isFabCanBeEnabled
                with(binding) {
                    when {
                        !text.toString().isLatinDigitOrUnderscore -> {
                            error = resources.getString(R.string.error_username_text)
                            layoutTag.boxStrokeColor = errorColor
                            layoutTag.setStartIconTintList(ColorStateList.valueOf(errorColor))
                        }
                        text?.isNotEmpty() == true && text?.length ?: 0 < tagMinLen -> {
                            error = resources.getString(R.string.eror_min_len, tagMinLen)
                            layoutTag.boxStrokeColor = errorColor
                            layoutTag.setStartIconTintList(ColorStateList.valueOf(errorColor))
                        }
                        else -> {
                            error = null
                            layoutTag.boxStrokeColor = neutralColor
                            layoutTag.setStartIconTintList(ColorStateList.valueOf(neutralColor))
                            viewModel.checkTag(tagMinLen) { editTag.text.toString() }
                            isBtnBlocked = true
                        }
                    }
                }
            }
            setText(channel?.tag)
        }
    }

    private fun setupEditDescription() {
        with(binding) {
            editDescription.apply {
                doOnTextChanged { _, _, _, _ ->
                    when {
                        text?.count { it.toString() == System.lineSeparator() } ?: 0 > 1 -> {
                            error = resources.getString(R.string.error_description_text)
                            layoutDescription.boxStrokeColor = errorColor
                            layoutDescription.setStartIconTintList(ColorStateList.valueOf(errorColor))
                        }
                        else -> {
                            layoutDescription.boxStrokeColor = successColor
                            layoutDescription.setStartIconTintList(
                                ColorStateList.valueOf(
                                    successColor
                                )
                            )
                        }
                    }
                }
                setText(channel?.description)
            }
        }
    }

    private fun setupBtnAvatar(){
        binding.btnAvatar.setOnClickListener{
            getImageLauncher.launch("image/*")
        }
    }

    private suspend fun createChannel(channel: IChannel) : IChannel?{
        try {
            viewModel.createChannel(channel)
            viewModel.parameter = channel.id
        }catch (t : Throwable){
            Toast.makeText(requireContext(),
                R.string.error_channel_create,
                Toast.LENGTH_SHORT
            ).show()
            return null
        }
        return channel
    }

    private suspend fun updateChannel(channel: IChannel,name : String, tag : String, desc : String) : Boolean{

        try {
            coroutineScope {
                listOf(
                    async {
                        if (channel.name != name)
                            viewModel.setName(name)
                    },
                    async {
                        if (channel.tag != tag)
                            viewModel.setTag(tag)
                    },
                    async {
                        if (channel.description != desc) {
                            viewModel.setDescription(desc)
                        }
                    },
                    async {
                        if (channel.imageUri != imageUri){
                            viewModel.setImageUri(imageUri.orEmpty())
                        }
                    }
                ).awaitAll()
            }
            return true
        }catch (t : Throwable){
            Toast.makeText(requireContext(),
                R.string.error_channel_edit,
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
    }
}