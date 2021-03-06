package net.teamredhawk.hawktool;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import net.teamredhawk.hawktool.Dialogs.Error_dialog;
import net.teamredhawk.hawktool.Dialogs.ToolDialogSelect;
import net.teamredhawk.hawktool.UtilsHelper.FileHelper;

public class SettingsActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public int theme;
    Boolean homeButton = false, themeChanged;
    Switch root, size, hide, date, tool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Selecciona el tema guardado por el usuario
        theme();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        //Configura el status bar y el toolbar
        toolbarStatusBar();
        setTitle(getResources().getString(R.string.action_settings));

        // Save current theme to use when user press dismiss inside dialog
        sharedPreferences = this.getSharedPreferences("VALUES", Context.MODE_PRIVATE);

        final Switch _switch = (Switch) findViewById(R.id.darkTheme);

        // Declaramos en esta parte los botones para ajustes.
        settingsButtons();

        if (sharedPreferences.getInt("THEME", 1) == 2){
            _switch.setChecked(true);
        }
        _switch.setOnCheckedChangeListener(
                new CheckBox.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            theme=2;
                            Context context = getApplicationContext();
                            CharSequence text = "encendido";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                        else {
                            theme=1;
                            Context context = getApplicationContext();
                            CharSequence text = "apagado";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                        sharedPreferences.edit().putBoolean("THEMECHANGED", true).apply();
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(SettingsActivity.this, Integer.toString(theme), duration);
                        toast.show();
                        settingThemeElection(theme);
                    }
                });

        // Checa si el tema fue cambiado para ajustar en el main activity y demas activitys
        themeChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item_post clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            if (!homeButton) {
                NavUtils.navigateUpFromSameTask(SettingsActivity.this);
            }
            if (homeButton) {
                if (!themeChanged) {
                    editor = sharedPreferences.edit();
                    editor.putBoolean("DOWNLOAD", false);
                    editor.apply();
                }
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void toolbarStatusBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void theme() {
        sharedPreferences = getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        theme = sharedPreferences.getInt("THEME", 1);
        settingTheme(theme);
    }

    public void settingTheme(int theme) {
        switch (theme) {
            case 1:
                setTheme(R.style.AppTheme);
                break;
            case 2:
                setTheme(R.style.AppTheme2);
                break;
            default:
                setTheme(R.style.AppTheme);
                break;
        }
    }


    public void settingThemeElection(int theme) {
        switch (theme) {
            case 1:
                editor = sharedPreferences.edit();
                editor.putInt("THEME", 1).apply();
                reiniciarActivity(this);
                break;
            case 2:
                editor = sharedPreferences.edit();
                editor.putInt("THEME", 2).apply();
                reiniciarActivity(this);
                break;
            default:
                editor = sharedPreferences.edit();
                editor.putInt("THEME", 1).apply();
                reiniciarActivity(SettingsActivity.this);
                break;
        }
    }

    private void themeChanged() {
        themeChanged = sharedPreferences.getBoolean("THEMECHANGED",false);
        homeButton = true;
    }

    //reinicia una Activity
    public static void reiniciarActivity(AppCompatActivity actividad){
        Intent intent=new Intent();
        intent.setClass(actividad, actividad.getClass());
        //llamamos a la actividad
        actividad.startActivity(intent);
        //finalizamos la actividad actual
        actividad.finish();
    }

    private void settingsButtons() {
        hide = (Switch) findViewById(R.id.hide_extension);
        hide.setChecked(sharedPreferences.getBoolean("HIDEEXTENSIONS",true));
        hide.setOnCheckedChangeListener(
                new CheckBox.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if(isChecked){
                            editor = sharedPreferences.edit();
                            editor.putBoolean("HIDEEXTENSIONS",true).apply();
                        }else{
                            editor = sharedPreferences.edit();
                            editor.putBoolean("HIDEEXTENSIONS",false).apply();
                        }
                    }
                });

        root = (Switch) findViewById(R.id.root_enable);
        root.setChecked(sharedPreferences.getBoolean("ROOTENABLE",false));
        root.setOnCheckedChangeListener(
                new CheckBox.OnCheckedChangeListener(){
                    public void onCheckedChanged(CompoundButton buttonView,boolean isChecked){
                        if(isChecked){
                            if(FileHelper.canRunRootCommands()){
                                editor = sharedPreferences.edit();
                                editor.putBoolean("ROOTENABLE",true).apply();
                            }else{
                                editor = sharedPreferences.edit();
                                editor.putBoolean("ROOTENABLE",false).apply();
                                root.setChecked(false);
                            }
                        }else {
                            editor = sharedPreferences.edit();
                            editor.putBoolean("ROOTENABLE",false).apply();
                        }
                        //Metodo
                    }
                });

        size = (Switch) findViewById(R.id.size_file_set);
        size.setChecked(sharedPreferences.getBoolean("SIZEFILES",false));
        size.setOnCheckedChangeListener(
                new CheckBox.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if(isChecked){
                            editor = sharedPreferences.edit();
                            editor.putBoolean("SIZEFILES",true).apply();
                        }else{
                            editor = sharedPreferences.edit();
                            editor.putBoolean("SIZEFILES",false).apply();
                        }
                    }
                });

        date= (Switch) findViewById(R.id.last_modified);
        date.setChecked(sharedPreferences.getBoolean("LASTMODIFIED",true));
        date.setOnCheckedChangeListener(
                new CheckBox.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if(isChecked){
                            editor = sharedPreferences.edit();
                            editor.putBoolean("LASTMODIFIED",true).apply();
                        }else{
                            editor = sharedPreferences.edit();
                            editor.putBoolean("LASTMODIFIED",false).apply();
                        }

                    }
                });

        tool= (Switch) findViewById(R.id.unpack_repack_enable);
        tool.setChecked(sharedPreferences.getBoolean("TOOLENABLE",false));
        tool.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    if (FileHelper.getStatusRoot(getApplicationContext())){
                        if(FileHelper.getBusyboxInstalled()){
                            FragmentManager fragmentManager= getSupportFragmentManager();
                            ToolDialogSelect tool2= new ToolDialogSelect();
                            tool2.setTool(tool);
                            tool2.setifExternal((FileHelper.getStoragePath(getApplicationContext(),true)) != null);
                            tool2.show(fragmentManager,"Select");
                        }else{
                            showErrorMessage(getString(R.string.Error_busy), Html.fromHtml(getResources().getString(R.string.Error_busy_not_install)));
                            editor = sharedPreferences.edit();
                            editor.putBoolean("TOOLENABLE",false).apply();
                            tool.setChecked(false);
                        }
                    }else{
                        showErrorMessage(getString(R.string.Error_root),getString(R.string.error_root_acces));
                        editor = sharedPreferences.edit();
                        editor.putBoolean("TOOLENABLE",false).apply();
                        tool.setChecked(false);
                    }
                }else{
                    editor = sharedPreferences.edit();
                    editor.putBoolean("TOOLENABLE",false).apply();
                }

            }
        });

    }

    private void showErrorMessage(String titulo, android.text.Spanned msg){
        FragmentManager fragmentManager = getSupportFragmentManager();
        Error_dialog dialog= new Error_dialog();
        dialog.setTitulo(titulo);
        dialog.setMessage(msg);
        dialog.setLink("stericson.busybox");
        dialog.show(fragmentManager, "ErrorDialog");
    }

    public void showErrorMessage(String titulo, String msg){
        FragmentManager fragmentManager = getSupportFragmentManager();
        Error_dialog dialog= new Error_dialog();
        dialog.setTitulo(titulo);
        dialog.setMessage(msg);
        dialog.show(fragmentManager, "ErrorDialog");
    }

}

