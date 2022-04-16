package com.goodvibes.multimessenger.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.goodvibes.multimessenger.AuthorizationTGActivity
import com.goodvibes.multimessenger.R

class AuthorizationTGInputNumber : Fragment() {
    lateinit var activity: AuthorizationTGActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_authorization_t_g_input_number, null)
        val buttonSubmitNumberTg = view.findViewById(R.id.button_submit_number_tg) as Button
        buttonSubmitNumberTg.setOnClickListener {
            val numberEditView = view.findViewById(R.id.edit_text_number_tg) as EditText
            val numberString = numberEditView.text.toString()
            activity.SendCodeForTGAuth(numberString)
            val trans = activity.supportFragmentManager.beginTransaction()
            val nextFragment = AuthorizationTGInputCode()
            trans.replace(R.id.fragment_container_auth_tg_activity, nextFragment)
            trans.commit()
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as AuthorizationTGActivity
    }
}