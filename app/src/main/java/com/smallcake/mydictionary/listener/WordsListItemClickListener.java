package com.smallcake.mydictionary.listener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.smallcake.mydictionary.R;
import com.smallcake.mydictionary.sqlite.WordsDataBase;
import com.smallcake.mydictionary.struct.Words;

import java.util.List;

public class WordsListItemClickListener implements AdapterView.OnItemClickListener {
    private Context mContext;
    private List<Words> mWords;

    public WordsListItemClickListener(Context context, List<Words> words){
        mContext = context;
        mWords = words;
    }

    private void updateAllWordsList(){
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent("load.all_words"));
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent("load.familiar_words"));
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent("load.new_words"));
    }

    private void dialogEditWords(final Words words){
        View DialogView = View.inflate(mContext, R.layout.dialog_edit_world, null);

        final EditText edWords = DialogView.findViewById(R.id.dialog_edit_world_edWords);
        final EditText edDescribe = DialogView.findViewById(R.id.dialog_edit_world_edDescribe);

        edWords.setText(words.getWords());
        edDescribe.setText(words.getDescribe());

        android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(mContext)
                .setView(DialogView)
                .setTitle("编辑")
                .setPositiveButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        words.setWords(edWords.getText().toString());
                        words.setDescribe(edDescribe.getText().toString());

                        (new WordsDataBase(mContext)).updateWord(words).close();

                        updateAllWordsList();
                    }
                })

                .setNegativeButton("取消",null)
                .create();

        dialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Words words = mWords.get(position);

        String[] items = new String[]{"熟悉","生词","编辑","删除","取消"};

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which){
                    case 0:
                        (new WordsDataBase(mContext)).setFamiliar(words,true).close();
                        updateAllWordsList();
                        break;

                    case 1:
                        (new WordsDataBase(mContext)).setFamiliar(words,false).close();
                        updateAllWordsList();
                        break;

                    case 2:
                        dialogEditWords(words);
                        break;

                    case 3:
                        (new WordsDataBase(mContext)).deleteWord(words).close();
                        updateAllWordsList();
                        break;
                }



            }
        });

        builder.create().show();

    }

}