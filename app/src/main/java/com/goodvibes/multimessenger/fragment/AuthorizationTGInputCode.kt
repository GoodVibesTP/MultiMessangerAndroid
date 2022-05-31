package com.goodvibes.multimessenger.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.goodvibes.multimessenger.AuthorizationTGActivity
import com.goodvibes.multimessenger.MainActivity
import com.goodvibes.multimessenger.R

class AuthorizationTGInputCode : Fragment() {
    lateinit var activity: AuthorizationTGActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_authorization_t_g_input_code, null)
        val buttonSubmitNumberTg = view.findViewById(R.id.button_submit_auth_code_tg) as Button
        buttonSubmitNumberTg.setOnClickListener {
            val codeEditView = view.findViewById(R.id.edit_text_code_tg) as EditText
            val codeString = codeEditView.text.toString()
            activity.CheckAuthCode(codeString)
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as AuthorizationTGActivity
    }
}