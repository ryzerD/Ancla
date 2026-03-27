package co.ryzer.ancla.ui.contacts

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.telephony.PhoneNumberUtils
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.util.Log
import java.util.Locale

private const val CONTACT_RESOLVER_TAG = "AnclaContactResolver"

data class PickedEmergencyContact(
    val displayName: String,
    val phoneNumber: String
)

fun resolvePickedContact(context: Context, contactUri: Uri): PickedEmergencyContact? {
    val resolver = context.contentResolver

    Log.d(CONTACT_RESOLVER_TAG, "pickContact uri=$contactUri")

    val baseContact = resolver.queryContactBase(contactUri) ?: return null
    val directNumber = resolver.queryPhoneFromPickedUri(contactUri)
    val contactIdFromUri = resolver.queryContactIdFromPickedUri(contactUri)
    val contactId = contactIdFromUri.ifBlank { baseContact.id }

    val phoneFromDataDirectory = resolver.queryPhoneFromContactUri(contactUri)
    val phoneFromContactId = if (contactId.isNotBlank()) resolver.queryPhoneFromContactId(contactId) else null

    Log.d(
        CONTACT_RESOLVER_TAG,
        "resolved baseName='${baseContact.displayName}', contactId='$contactId', directNumber='$directNumber', phoneFromDataDirectory='$phoneFromDataDirectory', phoneFromContactId='$phoneFromContactId'"
    )

    val normalizedPhone = normalizePhoneNumber(
        directNumber ?: phoneFromDataDirectory ?: phoneFromContactId
    )

    Log.d(CONTACT_RESOLVER_TAG, "normalizedPhone='$normalizedPhone'")

    if (baseContact.displayName.isBlank() && normalizedPhone.isBlank()) return null

    return PickedEmergencyContact(
        displayName = baseContact.displayName,
        phoneNumber = normalizedPhone
    )
}

private data class BaseContactInfo(
    val id: String,
    val displayName: String
)

private fun ContentResolver.queryContactBase(contactUri: Uri): BaseContactInfo? {
    return query(
        contactUri,
        arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME
        ),
        null,
        null,
        null
    )?.use { cursor ->
        if (!cursor.moveToFirst()) return null

        val id = cursor.optionalString(ContactsContract.Contacts._ID).orEmpty()
        val displayName = cursor.getString(
            cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)
        ).orEmpty()

        BaseContactInfo(
            id = id,
            displayName = displayName
        )
    }
}

private fun ContentResolver.queryPhoneFromPickedUri(contactUri: Uri): String? {
    return runCatching {
        query(
            contactUri,
            arrayOf(Phone.NUMBER),
            null,
            null,
            null
        )?.use { cursor ->
            if (!cursor.moveToFirst()) return@use null
            cursor.optionalString(Phone.NUMBER)
        }
    }.getOrNull()
}

private fun ContentResolver.queryContactIdFromPickedUri(contactUri: Uri): String {
    return runCatching {
        query(
            contactUri,
            arrayOf(Phone.CONTACT_ID, ContactsContract.Contacts._ID),
            null,
            null,
            null
        )?.use { cursor ->
            if (!cursor.moveToFirst()) return@use ""
            cursor.optionalString(Phone.CONTACT_ID)
                ?: cursor.optionalString(ContactsContract.Contacts._ID)
                ?: ""
        } ?: ""
    }.getOrDefault("")
}

private fun ContentResolver.queryPhoneFromContactUri(contactUri: Uri): String? {
    return runCatching {
        val dataUri = Uri.withAppendedPath(
            contactUri,
            ContactsContract.Contacts.Data.CONTENT_DIRECTORY
        )

        query(
            dataUri,
            arrayOf(Phone.NUMBER),
            "${ContactsContract.Data.MIMETYPE} = ?",
            arrayOf(Phone.CONTENT_ITEM_TYPE),
            "${Phone.IS_SUPER_PRIMARY} DESC, ${Phone.IS_PRIMARY} DESC"
        )?.use { cursor ->
            if (!cursor.moveToFirst()) return@use null
            cursor.getString(
                cursor.getColumnIndexOrThrow(Phone.NUMBER)
            )
        }
    }.getOrNull()
}

private fun ContentResolver.queryPhoneFromContactId(contactId: String): String? {
    return runCatching {
        query(
            Phone.CONTENT_URI,
            arrayOf(Phone.NUMBER),
            "${Phone.CONTACT_ID} = ?",
            arrayOf(contactId),
            "${Phone.IS_SUPER_PRIMARY} DESC, ${Phone.IS_PRIMARY} DESC"
        )?.use { cursor ->
            if (!cursor.moveToFirst()) return@use null
            cursor.getString(
                cursor.getColumnIndexOrThrow(Phone.NUMBER)
            )
        }
    }.getOrNull()
}

private fun android.database.Cursor.optionalString(columnName: String): String? {
    val index = getColumnIndex(columnName)
    return if (index >= 0) getString(index) else null
}

private fun normalizePhoneNumber(raw: String?): String {
    if (raw.isNullOrBlank()) return ""

    val normalizedInput = raw.trim()
    val regionIso = Locale.getDefault().country

    val formattedBySystem = runCatching {
        PhoneNumberUtils.formatNumber(normalizedInput, regionIso)
    }.getOrNull().orEmpty()

    val fallbackFormatted = formatPhoneFallback(normalizedInput)
    val candidate = if (formattedBySystem.isNotBlank()) formattedBySystem else fallbackFormatted

    return candidate
        .replace('-', ' ')
        .replace(Regex("\\s+"), " ")
        .trim()
}

private fun formatPhoneFallback(raw: String): String {
    val trimmed = raw.trim()
    val hasPlus = trimmed.startsWith("+")
    val digits = trimmed.filter { it.isDigit() }
    if (digits.isBlank()) return ""

    val groupedDigits = digits.chunked(3).joinToString(separator = " ")
    return if (hasPlus) "+$groupedDigits" else groupedDigits
}

