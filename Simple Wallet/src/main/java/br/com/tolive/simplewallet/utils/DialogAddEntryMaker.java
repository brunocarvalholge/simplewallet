package br.com.tolive.simplewallet.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

import br.com.tolive.simplewallet.app.R;
import br.com.tolive.simplewallet.constants.Constantes;
import br.com.tolive.simplewallet.db.EntryDAO;
import br.com.tolive.simplewallet.model.Entry;

/**
 * Created by bruno.carvalho on 04/07/2014.
 */
public class DialogAddEntryMaker {
    private static final String EMPTY = "0.00";
    private static final int DATE_YEAR = 2;
    private static final int DATE_MONTH = 1;
    private static final int DATE_DAY = 0;

    private OnClickOkListener mListener;
    private Context context;
    AlertDialog dialog;

    public DialogAddEntryMaker(Context context){
        this.context = context;
    }

    public AlertDialog makeAddDialog(){
        this.dialog = makeCustomAddDialog(null);
        setDialog(dialog);
        return dialog;
    }

    public AlertDialog makeAddDialog(Entry entry){
        this.dialog = makeCustomAddDialog(entry);
        setDialog(dialog);
        return dialog;
    }

    private void setDialog(AlertDialog dialog){
        this.dialog = dialog;
    }

    private String value = "";

    /**
     * After call this method, call setDialog to update dialog reference
     * so the button don't get NullPointeException.
     *
     * @return custom AlertDialog
     */
    private AlertDialog makeCustomAddDialog(final Entry entry) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        LayoutInflater inflater = (LayoutInflater)   context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_add, null);

        final EditText editTextDescription = (EditText) view.findViewById(R.id.dialog_add_edittext_description);
        final EditText editTextValue = (EditText) view.findViewById(R.id.dialog_add_edittext_value);
        final RadioGroup radioGroupType = (RadioGroup) view.findViewById(R.id.dialog_add_radiogroup_type);
        final RadioButton radioGain = (RadioButton) view.findViewById(R.id.dialog_add_radiobutton_gain);
        final RadioButton radioExpense = (RadioButton) view.findViewById(R.id.dialog_add_radiobutton_expense);

        editTextValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals(value)){
                    editTextValue.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("\\D+", "");

                    double parsed = Double.parseDouble(cleanString);
                    String formatted = NumberFormat.getCurrencyInstance().format((parsed / 100));

                    value = formatted;
                    editTextValue.setText(formatted);
                    editTextValue.setSelection(formatted.length());

                    editTextValue.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        editTextValue.setText("0");

        final LinearLayout containerChooseDate = (LinearLayout) view.findViewById(R.id.dialog_add_container_choose_date);
        TextView textChooseDate = (TextView) view.findViewById(R.id.dialog_add_text_choose);
        Typeface tf = Typeface.createFromAsset(context.getAssets(), Constantes.FONT_PATH_ROBOTO_CONDENSED_BOLD);
        textChooseDate.setTypeface(tf);
        if(entry != null){
            textChooseDate.setText(context.getResources().getString(R.string.dialog_add_text_choose_edit));
        }

        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.dialog_add_datepicker);

        containerChooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker.setVisibility(View.VISIBLE);
                containerChooseDate.setVisibility(View.GONE);
            }
        });

        TextView okButton = (TextView) view.findViewById(R.id.dialog_add_text_ok);
        TextView cancelButton = (TextView) view.findViewById(R.id.dialog_add_text_cancel);
        okButton.setTypeface(tf);
        cancelButton.setTypeface(tf);
        ThemeChanger.setDialogTheme(context, view, ThemeChanger.THEME_RED);

        if(entry != null){
            editTextDescription.setText(entry.getDescription());
            editTextValue.setText(String.format("%.2f", entry.getValue()));
            if (entry.getType() == Entry.TYPE_GAIN) {
                radioGain.setChecked(true);
                radioExpense.setChecked(false);
                ThemeChanger.setDialogTheme(context, view, ThemeChanger.THEME_GREEN);
            } else {
                radioGain.setChecked(false);
                radioExpense.setChecked(true);
                ThemeChanger.setDialogTheme(context, view, ThemeChanger.THEME_RED);
            }
            String[] split = entry.getDate().split("/");
            datePicker.updateDate(Integer.valueOf(split[DATE_YEAR]), Integer.valueOf(split[DATE_MONTH]) - 1, Integer.valueOf(split[DATE_DAY]));
        }

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonOk) {
                String description = editTextDescription.getText().toString();

                int typeRadioButtonId = radioGroupType.getCheckedRadioButtonId();
                int type = typeRadioButtonId == R.id.dialog_add_radiobutton_expense ? Entry.TYPE_EXPENSE : Entry.TYPE_GAIN;

                String category = "default";

                int month;
                String date;

                if (datePicker.getVisibility() == View.VISIBLE) {
                    month = datePicker.getMonth();
                    date = datePicker.getDayOfMonth() + "/" + (month + 1) + "/" + datePicker.getYear();
                } else {
                    if(entry == null) {
                        Calendar calendar = Calendar.getInstance();
                        month = calendar.get(Calendar.MONTH);
                        date = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (month + 1) + "/" + calendar.get(Calendar.YEAR);
                    } else{
                        date = entry.getDate();
                        month = Integer.valueOf(entry.getDate().split("/")[DATE_MONTH]) - 1;
                    }
                }

                String cleanValue = editTextValue.getText().toString().replaceAll("[\\D+]", "");
                cleanValue = new StringBuilder(cleanValue).insert(cleanValue.length()-2, ".").toString();
                if (cleanValue.equals(EMPTY)) {
                    Toast.makeText(context, R.string.dialog_add_invalid_value, Toast.LENGTH_SHORT).show();
                } else {
                    Float value = Float.parseFloat(cleanValue);
                    if (editTextDescription.getText().toString().equals(EMPTY)){
                        editTextDescription.setText(R.string.dialog_add_no_descripition);
                    }

                    EntryDAO dao = EntryDAO.getInstance(context);

                    if(entry == null) {
                        Entry newEntry = new Entry();
                        newEntry.setDescription(description);
                        newEntry.setValue(value);
                        newEntry.setType(type);
                        newEntry.setCategory(category);
                        newEntry.setDate(date);
                        newEntry.setMonth(month);

                        if (mListener != null) {
                            mListener.onClickOk(newEntry);
                            DialogAddEntryMaker.this.dialog.dismiss();
                        }
                    } else{
                        entry.setDescription(description);
                        entry.setValue(value);
                        entry.setType(type);
                        entry.setCategory(category);
                        entry.setDate(date);
                        entry.setMonth(month);

                        if (mListener != null) {
                            mListener.onClickOk(entry);
                            DialogAddEntryMaker.this.dialog.dismiss();
                        }
                    }
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonCancel) {
                DialogAddEntryMaker.this.dialog.cancel();
            }
        });

        radioGroupType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.dialog_add_radiobutton_gain:
                        ThemeChanger.setDialogTheme(context, view, ThemeChanger.THEME_GREEN);
                        break;
                    default:
                        ThemeChanger.setDialogTheme(context, view, ThemeChanger.THEME_RED);
                        break;
                }
            }
        });

        dialog.setView(view);
        return dialog.create();
    }

    private String formatToDot(String number) {
        if(number.contains(",")){
            return number.split(",")[0] + "." + number.split(",")[1];
        } else {
            return  number;
        }
    }


    public void setOnClickOkListener (OnClickOkListener listener){
        mListener = listener;
    }

    public interface OnClickOkListener {
        public void onClickOk(Entry entry);
    }

    /*private TextView getDialogTitleView(Typeface tf) {
        TextView titleView = new TextView(context);
        titleView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        titleView.setTypeface(tf);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, DIALOG_TITLE_SIZE);
        titleView.setTextColor(context.getResources().getColor(R.color.snow));
        titleView.setBackgroundColor(context.getResources().getColor(R.color.gray));
        titleView.setText(R.string.dialog_add_title);
        return titleView;
    }*/
}
