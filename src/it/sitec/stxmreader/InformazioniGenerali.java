package it.sitec.stxmreader;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

public class InformazioniGenerali extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_informazioni_generali);
		try
		{
			((TextView)findViewById(R.id.INFOGEN_tv_Versione_DATO)).setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName.toString());
			((TextView)findViewById(R.id.INFOGEN_tv_Revisione_DATO)).setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionCode + "");
			
		} catch (NameNotFoundException e) { }
	}
}
