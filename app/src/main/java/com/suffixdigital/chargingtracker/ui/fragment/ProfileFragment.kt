package com.suffixdigital.chargingtracker.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.suffixdigital.chargingtracker.R
import com.suffixdigital.chargingtracker.data.AgencyDataItem
import com.suffixdigital.chargingtracker.databinding.FragmentProfileBinding
import com.suffixdigital.chargingtracker.ui.activity.ChargingMainScreen
import com.suffixdigital.chargingtracker.ui.adapter.DropdownMenuAdapter
import com.suffixdigital.chargingtracker.utils.checkOffensiveWord
import com.suffixdigital.chargingtracker.utils.getAllTransitAgencies
import com.suffixdigital.chargingtracker.utils.inputFilter

class ProfileFragment : Fragment(), View.OnClickListener, AdapterView.OnItemClickListener,
    View.OnFocusChangeListener, AutoCompleteTextView.OnDismissListener {

    private lateinit var binding: FragmentProfileBinding


    private var isFirstNameValid: Boolean = false
    private var isLastNameValid: Boolean = false
    private var selectedTransitAgencyNumber: Int = 0

    /******************************************************************************
     * [isDropdownOpen] is a boolean variable. This variable is used to check does agency selection drop down menu is opened or not.
     * Default value of this variable is 'false'
     *****************************************************************************/
    private var isDropdownOpen: Boolean = false


    /******************************************************************************
     * [firstNameTextWatcher] is a instance of [TextInputEditText]. Here [TextInputEditText] has override class [TextWatcher] and it has
     * below over-ride functions to real time validate inserted data in [TextInputEditText]. Here This [TextWatcher] class is validate
     * entered FirstName is valid or not. If FirstName is not valid then display Error message to user
     *****************************************************************************/
    private val firstNameTextWatcher = object : TextWatcher {
        /******************************************************************************
         * [beforeTextChanged], No need to use this over-ride function here.
         *****************************************************************************/
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        /******************************************************************************
         * [onTextChanged], No need to use this over-ride function here.
         *****************************************************************************/
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        /******************************************************************************
         * [afterTextChanged], When user insert/delete character by character in [TextInputEditText] this function is validate does
         * FirstName is empty or not. If FirstName is valid then [isFirstNameValid] becomes true but if FirstName is not valid then
         * display Error message to user. Once FirstName, LastName, Email and Phone are valid then 'Signup' button becomes enable and
         * Change button color and text color.
         *****************************************************************************/
        override fun afterTextChanged(editable: Editable?) {
            when {
                /******************************************************************************
                 * If FirstName empty then display error message to user. [isFirstNameValid] becomes a false
                 *****************************************************************************/
                editable.toString().trim().isEmpty() -> {
                    isFirstNameValid = false
                    //displayErrorMessage(binding.tilFirstName, binding.etFirstName, resources.getString(R.string.err_empty_firstname))
                }

                checkOffensiveWord(editable?.trim().toString()) -> {
                    isFirstNameValid = false
                    //displayErrorMessage(binding.tilFirstName, binding.etFirstName, resources.getString(R.string.err_offensive_words_not_permitted))
                }

                editable.toString().length > 128 -> {
                    isFirstNameValid = false
                    //displayErrorMessage(binding.tilFirstName, binding.etFirstName, resources.getString(R.string.err_only_128_characters_allowed))
                }
                /******************************************************************************
                 * If FirstName is valid then [isFirstNameValid] becomes a True. The Signup button becomes enable and change button's background
                 * color and text color.
                 *****************************************************************************/
                else -> {
                    isFirstNameValid = true
                    binding.tilDriverName.isErrorEnabled = false
                    //enableSignupButton(context!!)
                }
            }
        }
    }

    /******************************************************************************
     * [lastNameTextWatcher] is a instance of [TextInputEditText]. Here [TextInputEditText] has override class [TextWatcher] and it has
     * below over-ride functions to real time validate inserted data in [TextInputEditText]. Here This [TextWatcher] class is validate
     * entered LastName is valid or not. If LastName is not valid then display Error message to user
     *****************************************************************************/
    private val lastNameTextWatcher = object : TextWatcher {
        /******************************************************************************
         * [beforeTextChanged], No need to use this over-ride function here.
         *****************************************************************************/
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        /******************************************************************************
         * [onTextChanged], No need to use this over-ride function here.
         *****************************************************************************/
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        /******************************************************************************
         * [afterTextChanged], When user insert/delete character by character in [TextInputEditText] this function is validate does
         * LastName is empty or not also LastName is with valid pattern or not. If LastName is valid then [isLastNameValid] becomes true but if
         * LastName is not valid then display Error message to user. Once FirstName, LastName, Email and Phone are valid then 'Signup' button
         * becomes enable and Change button color and text color.
         *****************************************************************************/
        override fun afterTextChanged(editable: Editable?) {
            when {
                /******************************************************************************
                 * If LastName empty then display error message to user. [isLastNameValid] becomes a false
                 *****************************************************************************/
                editable.toString().trim().isEmpty() -> {
                    isLastNameValid = false
                    //displayErrorMessage(binding.tilLastName, binding.etLastName, resources.getString(R.string.err_empty_lastname))
                }

                checkOffensiveWord(editable?.trim().toString()) -> {
                    isLastNameValid = false
//                    displayErrorMessage(binding.tilLastName, binding.etLastName, resources.getString(R.string.err_offensive_words_not_permitted))
                }

                editable.toString().length > 128 -> {
                    isLastNameValid = false
//                    displayErrorMessage(binding.tilLastName, binding.etLastName, resources.getString(R.string.err_only_128_characters_allowed))
                }
                /******************************************************************************
                 * If LastName is valid then [isLastNameValid] becomes a True. The Signup button becomes enable and change button's background
                 * color and text color.
                 *****************************************************************************/
                else -> {
                    isLastNameValid = true
                    binding.tilLicenseNumber.isErrorEnabled = false
//                    enableSignupButton(context!!)
                }
            }
        }
    }

    /******************************************************************************
     * [dropDownTextWatcher] is a instance of [AutoCompleteTextView]. Here [AutoCompleteTextView] has override class [TextWatcher] and it has
     * below over-ride functions to real time validate inserted data in [AutoCompleteTextView]. Here This [TextWatcher] class is validate does
     * user selected transit agency is valid or not from drop down menu. If selected agency is not valid then display error message to user.
     *****************************************************************************/
    private val dropDownTextWatcher = object : TextWatcher {
        /******************************************************************************
         * [beforeTextChanged], No need to use this over-ride function here.
         *****************************************************************************/
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        /******************************************************************************
         * [onTextChanged], No need to use this over-ride function here.
         *****************************************************************************/
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        /******************************************************************************
         * [afterTextChanged], When user insert/delete character by character in [AutoCompleteTextView] this function is validate does
         * Agency Name is empty or not. If selected agency is valid then [selectedTransitAgencyNumber] store agencyNumber of that agency. If agency name is null or empty
         * then display error message to user. Once 'FirstName', 'LastName', 'Email', 'PhoneNumber' and valid agency selected from drop down then
         * 'Sign Up' button becomes enable.
         *****************************************************************************/
        override fun afterTextChanged(editable: Editable?) {
            /******************************************************************************
             * Once user start to insert or delete character from [AutoCompleteTextView] field, required to reset [selectedTransitAgencyNumber] as '0'
             * This is indicate that user is interact with drop down menu.
             *****************************************************************************/
            selectedTransitAgencyNumber = 0

            /******************************************************************************
             * When user start to insert or delete character from [AutoCompleteTextView] at that time needs to check does [AutoCompleteTextView] is not empty or null.
             * If [editable] is not null or empty means user is interact with [AutoCompleteTextView] and not select any transit agency from suggested transit agencies list.
             * So user will show valid error message.
             *****************************************************************************/
            if (editable.toString().trim().isNotEmpty()) {
                //displayErrorMessage(binding.menu, null, resources.getString(R.string.err_invalid_agency_selection))
            }
            /******************************************************************************
             * When [editable] is null or empty that means user doesn't inserted any character in [AutoCompleteTextView].
             * Here user will show default agency selection drop down list.
             * [isDropdownOpen] becomes 'true'
             * Also visibility of 'error view' is gone.
             *****************************************************************************/
            else {
                if (binding.dropdownMenu.hasFocus()) {
                    binding.menu.setEndIconDrawable(R.drawable.arrow_drop_up)
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.dropdownMenu.showDropDown()
                        isDropdownOpen = true
                    }, 200)

                }
                binding.menu.isErrorEnabled = false
            }
            /******************************************************************************
             * Check does 'Sign Up' button is enable or not.
             *****************************************************************************/
            // enableSignupButton(context!!)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!::binding.isInitialized) {
            binding = FragmentProfileBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        binding.etDriverName.setText("Test Driver")
        binding.etLicenseNumber.setText("A1A2A3A4A5")
        binding.etPhone.setText("+91 xxxxxxxxxx")

        binding.etDriverName.filters = arrayOf(inputFilter)
        binding.etLicenseNumber.filters = arrayOf(inputFilter)
        //binding.etPhone.filters = arrayOf<InputFilter>(LengthFilter(phonePatternLength))
        binding.dropdownMenu.filters = arrayOf(inputFilter)

        binding.etDriverName.addTextChangedListener(firstNameTextWatcher)
        binding.etLicenseNumber.addTextChangedListener(lastNameTextWatcher)
        //binding.etPhone.addTextChangedListener(phoneTextWatcher)
        binding.dropdownMenu.addTextChangedListener(dropDownTextWatcher)

        binding.btnSave.setOnClickListener(this)
        binding.btnCancel.setOnClickListener(this)
        binding.dropdownMenu.setOnClickListener(this)
        binding.dropdownMenu.setOnDismissListener(this)
        binding.dropdownMenu.onFocusChangeListener = this

        val dropdownAdapter = DropdownMenuAdapter(
            requireContext(),
            R.layout.dropdown_child_item,
            getAllTransitAgencies(),
            binding
        )
        binding.dropdownMenu.onItemClickListener = this
        binding.dropdownMenu.setAdapter(dropdownAdapter)
        binding.dropdownMenu.threshold = 1
//        binding.dropdownMenu.setSelection(1)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_cancel, R.id.btn_save -> {
                startActivity(Intent(context, ChargingMainScreen::class.java))
                activity?.finish()
            }
        }
    }

    /******************************************************************************
     * When user select any agency from drop down menu [onItemClick] override method will call.
     * Once user select agency from drop down menu then
     * remove focus from [AutoCompleteTextView]. It will hide soft keyboard and hide drop down popup menu.
     * set selected agency name is [AutoCompleteTextView] and set cursor position at last index of selected agency name.
     * set selected agency's agencyNumber in [selectedTransitAgencyNumber].
     * Check 'Sign Up' button becomes enable or not for all valid user input field in Sign Up screen,
     *****************************************************************************/
    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        binding.dropdownMenu.clearFocus()
        val agencyItem = parent.adapter.getItem(position) as AgencyDataItem
        binding.dropdownMenu.setText(agencyItem.agencyName)
        binding.dropdownMenu.setSelection(agencyItem.agencyName.length)
        selectedTransitAgencyNumber = agencyItem.agencyNumber
        binding.menu.isErrorEnabled = false
    }


    /******************************************************************************
     * [onDismiss] is a override method of drop down menu. When drop down menu dismiss then this method will called.
     * once drop down menu dismiss then set [isDropdownOpen] value as 'false'
     *****************************************************************************/
    override fun onDismiss() {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.menu.setEndIconDrawable(R.drawable.arrow_drop_down)
            isDropdownOpen = false
        }, 200)
    }

    /******************************************************************************
     * [onFocusChange] is a override method of [AutoCompleteTextView]. When [AutoCompleteTextView] has focus then this override method will called.
     * When [AutoCompleteTextView] has focus then required to show drop down menu to user.
     * Set [isDropdownOpen] value as 'true'
     *****************************************************************************/
    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (hasFocus) {
            if (!isDropdownOpen) {
                binding.menu.setEndIconDrawable(R.drawable.arrow_drop_up)
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.dropdownMenu.showDropDown()
                    isDropdownOpen = true
                }, 200)
            }
        }
    }
}