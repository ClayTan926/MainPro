package com.ztwifi.account;

public class FindPasswordActivity extends VerPwdActivity {
	public static int resetCodeCvId = 710088;
	public static int resetCvId = 710089;


	@Override
	protected void doSetContentView() {
		setContentView(R.layout.act_find_password);
		this.vercodeCvId=resetCodeCvId;
		this.actCvId=resetCvId;
	}

}
