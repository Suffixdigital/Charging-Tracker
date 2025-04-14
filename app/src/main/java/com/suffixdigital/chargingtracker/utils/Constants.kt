package com.suffixdigital.chargingtracker.utils

import android.text.InputFilter
import android.text.Spanned
import com.suffixdigital.chargingtracker.data.AgencyDataItem
import com.suffixdigital.chargingtracker.data.StationData
import java.math.RoundingMode
import java.net.Socket
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Created by Kirtikant Patadiya on 2024-11-17.
 */

const val phonePattern: String = "(###) ###-####"
const val tfsPort: Int = 4000
var isSocketServerStarted = false


fun getChargingStationList(): MutableList<StationData> {
    return mutableListOf(
        StationData(1, "open", "Bharat Charge Hub", "Bharat Mandapam, Pragati Maidan, New Delhi"),
        StationData(2, "open", "Green Drive Station", "ARAI Campus, Kothrud, Pune"),
        StationData(3, "open", "EcoSpark Charging Point", "MG Road, Bengaluru"),
        StationData(4, "open", "ZapStation Central", "Connaught Place, New Delhi"),
        StationData(5, "open", "E-Volt Junction", "IIT Madras Research Park, Taramani, Chennai"),
        StationData(6, "busy", "VoltaPoint Green", "Satellite Road, Ahmedabad"),
        StationData(7, "busy", "PowerGrid EV Station", "Park Street, Kolkata"),
        /*       StationData(8, "open", "Static Charging Station"),
              StationData(9, "open", "Static Charging Station"),
             StationData(10, "open", "Static Charging Station"),
              StationData(11, "open", "Static Charging Station"),
              StationData(12, "busy", "Static Charging Station"),
              StationData(13, "busy", "Static Charging Station"),
              StationData(14, "open", "Static Charging Station"),
              StationData(15, "open", "Static Charging Station"),*/
    )
}

/*************************************************************************
 * Decimal Format up to 2 digit. like '$2.80'
 ************************************************************************/
val decimalFormat = DecimalFormat("#,##0.00").apply {
    roundingMode = RoundingMode.FLOOR
}

/******************************************************************************
 * Input filter to prevent leading and trailing spaces in an EditText.
 *
 * This input filter is designed to be applied to an EditText to restrict the input
 * such that it does not allow leading or trailing spaces. It achieves this by
 * checking the following conditions:
 *
 * 1. If the current input position (dstart) is 0 (beginning of the text),
 *    and the input source (source) is empty or contains only whitespace
 *    after trimming, then it returns an empty string (""). This effectively
 *    prevents the insertion of leading spaces.
 *
 * 2. In all other cases, it returns null, which indicates that the input is
 *    allowed.
 *
 * This input filter can be used to ensure that the text entered in an EditText
 * is always trimmed and does not contain any unnecessary leading or trailing
 * spaces.
 *****************************************************************************/
val inputFilter =
    InputFilter { source: CharSequence?, _: Int, _: Int, _: Spanned?, dstart: Int, _: Int ->
        if (dstart == 0 && source.toString().trim().isEmpty()) "" else null
    }

/******************************************************************************
 * [trimPhoneNumber] is used to remove regex from phone number. When phone number needs to use in
 * Retrofit API call then need to regex from phoneNumber
 *
 * @param phoneNumber with regex format Phone Number
 * @return String: removed regex from phone number
 *****************************************************************************/
fun trimPhoneNumber(phoneNumber: String): String {
    return phoneNumber.replace(Regex("[()\\-\\s]"), "").trim()
}

const val offensiveWords: String =
    "\\b(4r5e|5h1t|5hit|a55|anal|anus|ar5e|arrse|arse|ass|ass-fucker|asses|assfucker|assfukka|asshole|assholes|asswhole|a_s_s|b!tch|b00bs|b17ch|b1tch|ballbag|balls|ballsack|bastard|beastial|beastiality|bellend|bestial|bestiality|bi\\+ch|biatch|bitch|bitcher|bitchers|bitches|bitchin|bitching|bloody|blow job|blowjob|blowjobs|boiolas|bollock|bollok|boner|boob|boobs|booobs|boooobs|booooobs|booooooobs|breasts|buceta|bugger|bum|bunny fucker|butt|butthole|buttmuch|buttplug|c0ck|c0cksucker|carpet muncher|cawk|chink|cipa|cl1t|clit|clitoris|clits|cnut|cock|cock-sucker|cockface|cockhead|cockmunch|cockmuncher|cocks|cocksuck|cocksucked|cocksucker|cocksucking|cocksucks|cocksuka|cocksukka|cok|cokmuncher|coksucka|coon|cox|crap|cum|cummer|cumming|cums|cumshot|cunilingus|cunillingus|cunnilingus|cunt|cuntlick|cuntlicker|cuntlicking|cunts|cyalis|cyberfuc|cyberfuck|cyberfucked|cyberfucker|cyberfuckers|cyberfucking|d1ck|damn|dick|dickhead|dildo|dildos|dink|dinks|dirsa|dlck|dog-fucker|doggin|dogging|donkeyribber|doosh|duche|dyke|ejaculate|ejaculated|ejaculates|ejaculating|ejaculatings|ejaculation|ejakulate|f u c k|f u c k e r|f4nny|fag|fagging|faggitt|faggot|faggs|fagot|fagots|fags|fanny|fannyflaps|fannyfucker|fanyy|fatass|fcuk|fcuker|fcuking|feck|fecker|felching|fellate|fellatio|fingerfuck|fingerfucked|fingerfucker|fingerfuckers|fingerfucking|fingerfucks|fistfuck|fistfucked|fistfucker|fistfuckers|fistfucking|fistfuckings|fistfucks|flange|fook|fooker|fuck|fucka|fucked|fucker|fuckers|fuckhead|fuckheads|fuckin|fucking|fuckings|fuckingshitmotherfucker|fuckme|fucks|fuckwhit|fuckwit|fudge packer|fudgepacker|fuk|fuker|fukker|fukkin|fuks|fukwhit|fukwit|fux|fux0r|f_u_c_k|gangbang|gangbanged|gangbangs|gaylord|gaysex|goatse|God|god-dam|god-damned|goddamn|goddamned|hardcoresex|hell|heshe|hoar|hoare|hoer|homo|hore|horniest|horny|hotsex|jack-off|jackoff|jap|jerk-off|jism|jiz|jizm|jizz|kawk|knob|knobead|knobed|knobend|knobhead|knobjocky|knobjokey|kock|kondum|kondums|kum|kummer|kumming|kums|kunilingus|l3i\\+ch|l3itch|labia|lust|lusting|m0f0|m0fo|m45terbate|ma5terb8|ma5terbate|masochist|master-bate|masterb8|masterbat*|masterbat3|masterbate|masterbation|masterbations|masturbate|mo-fo|mof0|mofo|mothafuck|mothafucka|mothafuckas|mothafuckaz|mothafucked|mothafucker|mothafuckers|mothafuckin|mothafucking|mothafuckings|mothafucks|mother fucker|motherfuck|motherfucked|motherfucker|motherfuckers|motherfuckin|motherfucking|motherfuckings|motherfuckka|motherfucks|muff|mutha|muthafecker|muthafuckker|muther|mutherfucker|n1gga|n1gger|nazi|nigg3r|nigg4h|nigga|niggah|niggas|niggaz|nigger|niggers|nob|nob jokey|nobhead|nobjocky|nobjokey|numbnuts|nutsack|orgasim|orgasims|orgasm|orgasms|p0rn|pawn|pecker|penis|penisfucker|phonesex|phuck|phuk|phuked|phuking|phukked|phukking|phuks|phuq|pigfucker|pimpis|piss|pissed|pisser|pissers|pisses|pissflaps|pissin|pissing|pissoff|poop|porn|porno|pornography|pornos|prick|pricks|pron|pube|pusse|pussi|pussies|pussy|pussys|rectum|retard|rimjaw|rimming|s hit|s.o.b.|sadist|schlong|screwing|scroat|scrote|scrotum|semen|sex|sh!\\+|sh!t|sh1t|shag|shagger|shaggin|shagging|shemale|shi\\+|shit|shitdick|shite|shited|shitey|shitfuck|shitfull|shithead|shiting|shitings|shits|shitted|shitter|shitters|shitting|shittings|shitty|skank|slut|sluts|smegma|smut|snatch|son-of-a-bitch|spac|spunk|s_h_i_t|t1tt1e5|t1tties|teets|teez|testical|testicle|tit|titfuck|tits|titt|tittie5|tittiefucker|titties|tittyfuck|tittywank|titwank|tosser|turd|tw4t|twat|twathead|twatty|twunt|twunter|v14gra|v1gra|vagina|viagra|vulva|w00se|wang|wank|wanker|wanky|whoar|whore|willies|willy|xrated|xxx|analannie|analsex|arsehole|assbagger|assblaster|assclown|asscowboy|assfuck|asshat|asshore|assjockey|asskiss|asskisser|assklown|asslick|asslicker|asslover|assman|assmonkey|assmunch|assmuncher|asspacker|asspirate|asspuppies|assranger|asswhore|asswipe|badfuck|bigbastard|bigbutt|bitchez|bitchslap|bitchy|bondage|bong|boobies|booty|bootycall|bullshit|bumblefuck|bumfuck|buttbang|butt-bang|buttface|buttfuck|butt-fuck|buttfucker|butt-fucker|buttfuckers|butt-fuckers|butthead|buttman|buttmunch|buttmuncher|buttpirate|buttstain|chinky|cockblock|cockblocker|cockcowboy|cockfight|cockknob|cocklicker|cocklover|cocknob|cockqueen|cockrider|cocksman|cocksmith|cocksmoker|cocksucer|cocktease|crackwhore|crack-whore|cunteyed|cuntfuck|cuntfucker|cuntsucker|datnigga|dickbrain|dickforbrains|dickless|dicklick|dicklicker|dickman|dickwad|dickweed|dipshit|dripdick|dumbass|dumbbitch|dumbfuck|eatballs|eatpussy|facefucker|fastfuck|fatfuck|fatfucker|fckcum|felatio|fisting|footfuck|footfucker|foreskin|freakfuck|freakyfucker|freefuck|fuckable|fuckbag|fuckbuddy|fuckedup|fuckface|fuckfest|fuckfreak|fuckfriend|fuckher|fuckina|fuckingbitch|fuckinnuts|fuckinright|fuckit|fuckknob|fuckmehard|fuckmonkey|fuckoff|fuckpig|fucktard|fuckwhore|fuckyou|funfuck|gangbanger|gaymuthafuckinwhore|godammit|goddamit|goddammit|goddamnes|goddamnit|goddamnmuthafucker|goldenshower|handjob|headfuck|hiscock|horseshit|hotpussy|iblowu|jackshit|jerkoff|jizzim|jizzum|limpdick|mastabate|mastabater|masterblaster|mastrabator|masturbating|motherlovebone|nastybitch|nastywhore|nigg|niggaracci|niggard|niggarded|niggarding|niggardliness|niggardliness's|niggardly|niggards|niggard's|niggerhead|niggerhole|nigger's|niggle|niggled|niggles|niggling|nigglings|niggor|niggur|nofuckingway|nutfucker|peni5|penile|penises|pindick|pornflick|pornking|pornprincess|puntang|puss|pussie|pussyeater|pussyfucker|pussylicker|pussylips|pussylover|pussypounder|rentafuck|sandnigger|sexwhore|shitcan|shiteater|shitface|shitfaced|shitfit|shitforbrains|shitfucker|shithapens|shithappens|shithouse|shitlist|shitola|shitoutofluck|shitstain|shortfuck|skankbitch|skankfuck|skankwhore|skankybitch|skankywhore|slutwhore|snigger|sniggered|sniggering|sniggers|snigger's|snownigger|sonofabitch|sonofbitch|spaghettinigger|stupidfuck|stupidfucker|suckdick|suckmyass|suckmydick|suckmytit|timbernigger|titbitnipply|titfucker|titfuckin|titjob|titlicker|titlover|tittie|titty|twobitwhore|unfuckable|upthebutt|wanking|whiskeydick|whiskydick|whitenigger|whorefucker|whorehouse|williewanker)\\b"


//var receiverIPAddress: String = "192.168.1.101"

//var receiverIPAddress: String = "192.168.56.105"
var receiverIPAddress: String = "0.0.0.0"
var socket: Socket? = null

var battery_percentage = 0
var total_amount: String = ""
var power_consumed: String = "0"
var totalChargingTime = 0
var chargingID: Int? = 1

var connecting_time: Long = 25
var startCharging_time: Long = 20

/*************************************************************************
 * Retrofit API
 ************************************************************************/
const val timeout_debug = 600L
const val timeout = 600L

//MMM dd, yyyy hh:mm aa

var startTime = SimpleDateFormat(
    "MM/dd/yyyy hh:mm:ss aa",
    Locale.getDefault()
).format(Calendar.getInstance().time)
var endTime = SimpleDateFormat(
    "MM/dd/yyyy hh:mm:ss aa",
    Locale.getDefault()
).format(Calendar.getInstance().time)

/******************************************************************************
 * Checks if a given input string contains any offensive words.
 *
 * This function utilizes a predefined set of offensive words (`offensiveWords`)
 * and a regular expression to determine if the input string contains any
 * of these words, regardless of their case.
 *
 * It works as follows:
 * 1. It converts the `offensiveWords` string into a regular expression using
 *    `toRegex()`.
 * 2. It sets the `RegexOption.IGNORE_CASE` option to ensure that the
 *    regular expression matches offensive words regardless of their case.
 * 3. It uses the `containsMatchIn()` function of the regular expression to
 *    check if the input string contains any matches.
 * 4. It returns `true` if the input string contains any offensive words,
 *    and `false` otherwise.
 *
 * @param customInput The input string to check for offensive words.
 * @return `true` if the input string contains any offensive words,
 * `false` otherwise.
 *****************************************************************************/
fun checkOffensiveWord(customInput: String): Boolean =
    offensiveWords.toRegex(setOf(RegexOption.IGNORE_CASE)).containsMatchIn(customInput)

fun getAllTransitAgencies(): MutableList<AgencyDataItem> {
    return mutableListOf<AgencyDataItem>().apply {
        add(AgencyDataItem(1, "Vehicle 1", "", "Vehicle 1", ""))
        add(AgencyDataItem(2, "Vehicle 2", "", "Vehicle 2", ""))
        add(AgencyDataItem(3, "Vehicle 3", "", "Vehicle 3", ""))
        add(AgencyDataItem(4, "Vehicle 4", "", "Vehicle 4", ""))
        add(AgencyDataItem(5, "Vehicle 5", "", "Vehicle 5", ""))
        add(AgencyDataItem(6, "Vehicle 6", "", "Vehicle 6", ""))
        add(AgencyDataItem(7, "Vehicle 7", "", "Vehicle 7", ""))
        add(AgencyDataItem(8, "Vehicle 8", "", "Vehicle 8", ""))
        add(AgencyDataItem(9, "Vehicle 9", "", "Vehicle 9", ""))
        add(AgencyDataItem(10, "Vehicle 10", "", "Vehicle 10", ""))
        add(AgencyDataItem(11, "Vehicle 11", "", "Vehicle 11", ""))
    }
}
