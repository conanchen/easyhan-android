package org.ditto.feature.login;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import org.ditto.feature.login.controllers.RegisterController;
import org.ditto.feature.base.BaseFragment;
import org.ditto.lib.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RegisterUsernameFragment extends BaseFragment {
    private RegisterController.Callbacks callbacks;

    @BindView(R2.id.register_username)
    AutoCompleteTextView mRegisterUsernameText;


    public RegisterUsernameFragment() {
        // Required empty public constructor
    }

    public static RegisterUsernameFragment create(String title) {
        RegisterUsernameFragment fragment = new RegisterUsernameFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TITLE, title);
        fragment.setArguments(bundle);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.register_username_fragment, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RegisterController.Callbacks) {
            callbacks = (RegisterController.Callbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement RegisterController.ContentCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    @OnClick(R2.id.register_button)
    public void onRegisterButtonClicked() {
        //TODO: call server api to register username+smsauthcode
        boolean ok = true;
        if (ok) {
            callbacks.onUsernameDone(mRegisterUsernameText.getText().toString());
        }
    }


}
