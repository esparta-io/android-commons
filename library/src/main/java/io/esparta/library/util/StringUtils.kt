package io.esparta.library.util

import android.content.Context
import android.text.Editable
import android.text.Html
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.EditText
import com.github.rtoshiro.util.format.SimpleMaskFormatter
import com.terrakok.phonematter.PhoneFormat
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import java.text.NumberFormat
import java.util.*
import java.util.regex.Pattern

val cpfFormat: SimpleMaskFormatter by lazy { SimpleMaskFormatter("NNN.NNN.NNN-NN") }

val zipCodeFormat: SimpleMaskFormatter by lazy { SimpleMaskFormatter("NN NNN-NNN") }

val creditCardNumberFormat: SimpleMaskFormatter by lazy { SimpleMaskFormatter("NNNN NNNN NNNN NNNNNNN") }

val creditCardExpirationFormat: SimpleMaskFormatter by lazy { SimpleMaskFormatter("NN/NNNN") }

fun isEmailField(email: String?): Boolean {
    if (email == null) {
        return false
    }

    if (email.isEmpty() ||
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        return false
    }

    return true
}

fun unmaskNumbers(s: String?): String = s?.replace("[^0-9]*".toRegex(), "") ?: ""

@Suppress("LocalVariableName")
fun cpfIsValid(value: String?): Boolean{
    var cpf = value
    val PATTERN_GENERIC = Pattern.compile("[0-9]{3}\\.?[0-9]{3}\\.?[0-9]{3}\\-?[0-9]{2}")
    val PATTERN_NUMBERS = Pattern.compile("(?=^((?!((([0]{11})|([1]{11})|([2]{11})|([3]{11})|([4]{11})|([5]{11})|([6]{11})|([7]{11})|([8]{11})|([9]{11})))).)*$)([0-9]{11})")

    if (cpf != null && PATTERN_GENERIC.matcher(cpf).matches()) {
        cpf = cpf.replace(Regex("-|\\."), "")
        if (PATTERN_NUMBERS.matcher(cpf).matches()) {
            val numbers = IntArray(11)
            for (i in 0..10) numbers[i] = Character.getNumericValue(cpf[i])
            var i: Int
            var sum = 0
            var factor = 100
            i = 0
            while (i < 9) {
                sum += numbers[i] * factor
                factor -= 10
                i++
            }
            var leftover: Int = sum % 11
            leftover = if (leftover == 10) 0 else leftover
            if (leftover == numbers[9]) {
                sum = 0
                factor = 110
                i = 0
                while (i < 10) {
                    sum += numbers[i] * factor
                    factor -= 10
                    i++
                }
                leftover = sum % 11
                leftover = if (leftover == 10) 0 else leftover
                return leftover == numbers[10]
            }
        }
    }
    return false
}

private val moneyFormatter by lazy { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }

fun formatCurrency(value: Double?): String {
    value?.let {
        return moneyFormatter.format(it)
    }
    return ""
}

fun formatTwoDigitsFilter(): InputFilter {
    return InputFilter { source, start, end, dest, dstart, dend ->
        val builder = StringBuilder(dest)
        builder.replace(dstart, dend, source.subSequence(start, end).toString())
        if (!builder.toString().matches(Regex("(([1-9]{1})([0-9]{0,4})?(\\.)?)?([0-9]{0,2})?"))) {
            if (source.isEmpty()) {
                return@InputFilter dest.subSequence(dstart, dend)
            }
            return@InputFilter ""
        }
        return@InputFilter null
    }
}



@Suppress("DEPRECATION")
fun stripHtml(html: String): String {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString()
    } else {
        Html.fromHtml(html).toString()
    }
}

fun splitNameLastName(nameToSplit: String): Pair<String, String> {
    val tmpName = nameToSplit.split(" ")[0].take(50)
    val tmpLastName = nameToSplit.replace(tmpName, "").trim().take(50)
    return Pair(tmpName, tmpLastName)
}

fun phoneWatcher(editText: EditText): TextWatcher {
    return object : TextWatcher {

        var lastEdit: String? = null

        override fun afterTextChanged(editable: Editable?) {
            editable?.let {
                if (it.toString() != lastEdit) {
                    lastEdit = formatBrazilianPhoneNumber(editText.context, it.toString())
                    editText.removeTextChangedListener(this)
                    editText.setText(lastEdit)
                    lastEdit?.let { editText.setSelection(it.length) }
                    editText.addTextChangedListener(this)
                }
            }
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }
    }
}

fun formatBrazilianPhoneNumber(context: Context, rawPhone: String?): String? {
    val phoneFormat = PhoneFormat("br", context)

    phoneFormat.defaultCallingCode
    rawPhone?.let {
        return phoneFormat.format(rawPhone)
    }

    return null
}

fun getPhonePrefixAndNumber(phone: String?): Pair<String, String> {
    if (phone == null || phone.isBlank()) return Pair("", "")

    val stripped = unmaskNumbers(phone)
    val prefix = stripped.take(2)
    val number = stripped.drop(2)

    return Pair(prefix, number)
}

/**
 * @param pattern dd.MM.yyyy, dd/MM/yyyy, etc
 */
fun formatDateShortDate(date: DateTime?, pattern: String): String {
    date?.let {
        val dtfOut = DateTimeFormat.forPattern(pattern)
        return dtfOut.print(date)
    }
    return ""
}

fun formatDateShortDate(date: LocalDate?, pattern: String): String {
    date?.let {
        val dtfOut = DateTimeFormat.forPattern(pattern)
        return dtfOut.print(date)
    }
    return ""
}