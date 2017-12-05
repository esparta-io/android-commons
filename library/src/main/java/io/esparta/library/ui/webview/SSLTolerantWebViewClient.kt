package io.esparta.library.ui.webview

import android.net.http.SslError
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import android.content.DialogInterface

/**
 *
 * Created by gmribas on 04/12/17.
 */
class SSLTolerantWebViewClient(private val activity: AppCompatActivity, private     val ignoreAll: Boolean = false): WebViewClient() {

    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        if (ignoreAll) {
            handler?.proceed()
            return
        }

        var message = "SSL Certificate error."

        when (error?.primaryError) {
            SslError.SSL_UNTRUSTED -> {
                message = "The certificate authority is not trusted."
            }
            SslError.SSL_EXPIRED -> {
                message = "The certificate has expired."
            }
            SslError.SSL_IDMISMATCH -> {
                message = "The certificate Hostname mismatch."
            }
            SslError.SSL_NOTYETVALID -> {
                message = "The certificate is not yet valid."
            }
        }

        val builder = AlertDialog.Builder(activity)
        val alertDialog = builder.create()

        message += " Do you want to continue anyway?"
        alertDialog.setTitle("SSL Certificate Error")
        alertDialog.setMessage(message)
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK") { _, _ ->
            // Ignore SSL certificate errors
            handler?.proceed()
        }

        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel") { _, _ -> handler?.cancel() }
        alertDialog.show()
    }
}