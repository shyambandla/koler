package com.chooloo.www.chooloolib.interactor.navigation

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.provider.ContactsContract
import android.telecom.TelecomManager
import android.telephony.PhoneNumberUtils
import com.chooloo.www.chooloolib.R
import com.chooloo.www.chooloolib.interactor.string.StringsInteractor
import com.chooloo.www.chooloolib.ui.base.BaseActivity
import com.chooloo.www.chooloolib.util.baseobservable.BaseObservable
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationsInteractorImpl @Inject constructor(
    private val strings: StringsInteractor,
    private val telecomManager: TelecomManager,
    @ApplicationContext private val context: Context,
) :
    BaseObservable<NavigationsInteractor.Listener>(),
    NavigationsInteractor {



    override fun goToAppGithub() {
        context.startActivity(
            Intent(
                ACTION_VIEW,
                Uri.parse(strings.getString(R.string.app_source_url))
            ).addFlags(FLAG_ACTIVITY_NEW_TASK)
        )
    }

    override fun manageBlockedNumber() {
        context.startActivity(
            telecomManager.createManageBlockedNumbersIntent()
                .addFlags(FLAG_ACTIVITY_NEW_TASK),
            null
        )
    }

    override fun goToLauncherActivity() {
        context.startActivity(
            context.packageManager.getLaunchIntentForPackage(context.packageName)
                ?.addFlags(FLAG_ACTIVITY_CLEAR_TASK)
        )
    }

    override fun sendSMS(number: String?) {
        context.startActivity(
            Intent(
                ACTION_SENDTO,
                Uri.parse("smsto:${PhoneNumberUtils.normalizeNumber(number)}")
            ).addFlags(FLAG_ACTIVITY_NEW_TASK)
        )
    }

    override fun addContact(number: String) {
        context.startActivity(
            Intent(ACTION_INSERT)
                .setType(ContactsContract.Contacts.CONTENT_TYPE)
                .putExtra(ContactsContract.Intents.Insert.PHONE, number)
                .addFlags(FLAG_ACTIVITY_NEW_TASK)
        )
    }

    override fun viewContact(contactId: Long) {
        context.startActivity(
            Intent(ACTION_VIEW)
                .setData(
                    Uri.withAppendedPath(
                        ContactsContract.Contacts.CONTENT_URI,
                        contactId.toString()
                    )
                )
                .addFlags(FLAG_ACTIVITY_NEW_TASK)
        )
    }

    override fun goToActivity(activityClass: Class<out BaseActivity<*>>) {
        context.startActivity(
            Intent(context, activityClass)
                .addFlags(FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TOP or FLAG_ACTIVITY_CLEAR_TASK)
        )
    }

    override fun editContact(contactId: Long) {
        context.startActivity(
            Intent(
                ACTION_EDIT,
                ContactsContract.Contacts.CONTENT_URI
            )
                .setData(
                    ContentUris.withAppendedId(
                        ContactsContract.Contacts.CONTENT_URI,
                        contactId
                    )
                )
                .addFlags(FLAG_ACTIVITY_NEW_TASK)
        )
    }
}