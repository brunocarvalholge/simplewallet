package br.com.tolive.simplewalletpro.app;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Calendar;

import br.com.tolive.simplewalletpro.R;
import br.com.tolive.simplewalletpro.constants.Constants;
import br.com.tolive.simplewalletpro.db.EntryDAO;
import br.com.tolive.simplewalletpro.model.Entry;
import br.com.tolive.simplewalletpro.utils.DialogAddEntryMaker;
import br.com.tolive.simplewalletpro.utils.RecurrentsManager;
import br.com.tolive.simplewalletpro.views.CustomTextView;

public class AddFragment extends Fragment {

    private ImageView buttonAdd;
    private CustomTextView textBalance;
    private CustomTextView textGain;
    private CustomTextView textExpense;
    private RelativeLayout background;
    private EntryDAO dao;
    private RecurrentsManager recurrentsManager;

    private AlertDialog dialog;
    private int currentMonth;

    public AddFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add, container, false);

        dao = EntryDAO.getInstance(getActivity());
        recurrentsManager = new RecurrentsManager(getActivity());

        buttonAdd = (ImageView) rootView.findViewById(R.id.fragment_add_button_add);
        textBalance = (CustomTextView) rootView.findViewById(R.id.fragment_add_text_balance);
        textGain = (CustomTextView) rootView.findViewById(R.id.fragment_add_text_gain);
        textExpense = (CustomTextView) rootView.findViewById(R.id.fragment_add_text_expense);
        background = (RelativeLayout) rootView.findViewById(R.id.fragment_add_background);

        Calendar calendar = Calendar.getInstance();
        currentMonth = calendar.get(Calendar.MONTH);

        refreshBalanceText(currentMonth);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogAddEntryMaker dialogAddEntryMaker = new DialogAddEntryMaker(getActivity());
                dialogAddEntryMaker.setOnClickOkListener(new DialogAddEntryMaker.OnClickOkListener() {
                    @Override
                    public void onClickOk(Entry entry, int recurrency) {
                        long id = 0;
                        if(recurrency == RecurrentsManager.RECURRENT_NONE) {
                            id = dao.insert(entry);
                            Toast.makeText(getActivity(), R.string.dialog_add_sucess, Toast.LENGTH_SHORT).show();
                            if(entry.getMonth() == currentMonth) {
                                refreshBalanceText(currentMonth);
                            }
                        }
                        if (id != -1 && recurrency != RecurrentsManager.RECURRENT_NONE) {
                            entry.setId(id);
                            int month = entry.getMonth();
                            recurrentsManager.insert(entry, recurrency);
                            Toast.makeText(getActivity(), R.string.dialog_add_sucess, Toast.LENGTH_SHORT).show();
                            refreshBalanceText(month);
                        } else if (id == -1){
                            Toast.makeText(getActivity(), R.string.dialog_add_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog = dialogAddEntryMaker.makeAddDialog();

                dialog.show();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshBalanceText(currentMonth);
    }

    private void refreshBalanceText(int month) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        int balanceType = sharedPreferences.getInt(Constants.SP_KEY_BALANCE_TYPE, Constants.SP_BALANCE_TYPE_DEFAULT);
        if (balanceType == Constants.BALANCE_TYPE_TOTAL) {
            month = EntryDAO.ALL;
        }
        Float balance = new Float(dao.getMonthBalance(month));
        Float gain = dao.getGain(month);
        Float expense = dao.getExpense(month);
        refreshBackGround(gain, expense);
        textBalance.setText("Saldo: " + String.format("%.2f", balance));
        textGain.setText("Ganho: " + String.format("%.2f", gain));
        textExpense.setText("Despesa: " + String.format("%.2f", expense));
    }

    private void refreshBackGround(Float gain, Float expense){

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        float yellow = sharedPreferences.getFloat(Constants.SP_KEY_YELLOW, Constants.SP_YELLOW_DEFAULT);
        float red = sharedPreferences.getFloat(Constants.SP_KEY_RED, Constants.SP_RED_DEFAULT);

        ActionBar actionBar = getActivity().getActionBar();

        int color;
        if((gain - expense) < red){
            actionBar.setIcon(R.drawable.ic_title_red);
            color = getActivity().getResources().getColor(R.color.red);
            buttonAdd.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.button_add_red));
        } else if((gain - expense) < yellow){
            actionBar.setIcon(R.drawable.ic_title_yellow);
            color = getActivity().getResources().getColor(R.color.yellow);
            buttonAdd.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.button_add_yellow));
        } else{
            actionBar.setIcon(R.drawable.ic_title_green);
            color = getActivity().getResources().getColor(R.color.green);
            buttonAdd.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.button_add_green));
        }

        background.setBackgroundColor(color);
    }
}