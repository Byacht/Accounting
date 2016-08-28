package com.example.dn.accounting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ht.sqlite.MyDataBaseHelper;

import net.youmi.android.AdManager;
import net.youmi.android.normal.banner.BannerManager;

import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    public static final int DELETE_DATA = 0;
    public static final int UPDATE_DATA = 1;

    private TextView allCost;
    private EditText eventsEdit;
    private EditText costEdit;
    private EditText updateEventEdit;
    private EditText updateCostEdit;
    private Button submit;
    private ListView listView;
    private ArrayList<AccountsMessage> messages;
    private AccountsAdapter adapter;
    private MyDataBaseHelper myDataBaseHelper;
    private View updateAccountsView;
    private PullToRefreshListView refreshListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AdManager.getInstance(this).init("7a3005bf37e8f523","ead3f10f78d624c2",false,true);
        View adView = BannerManager.getInstance(this).getBanner(this);
        LinearLayout adLayout = (LinearLayout) findViewById(R.id.adLayout);
        adLayout.addView(adView);

        allCost = (TextView) findViewById(R.id.cost_textview);
        eventsEdit = (EditText) findViewById(R.id.edit_events);
        costEdit = (EditText) findViewById(R.id.edit_cost);
        updateAccountsView = LayoutInflater.from(this).inflate(R.layout.update_edit, null);
        updateEventEdit = (EditText) updateAccountsView.findViewById(R.id.update_edit_events);
        updateCostEdit = (EditText) updateAccountsView.findViewById(R.id.update_edit_cost);
        submit = (Button) findViewById(R.id.submitbtn);
//        listView = (ListView) findViewById(R.id.accounts_list);

        myDataBaseHelper = new MyDataBaseHelper(this, "Accounts.db", null, 1);

        messages = new ArrayList<AccountsMessage>();

        adapter = new AccountsAdapter(this, R.layout.accounts_list, messages);
//        listView.setAdapter(adapter);

        refreshAccountsList();

        refreshListView = (PullToRefreshListView) findViewById(R.id.refresh_listview);
        refreshListView.setAdapter(adapter);
        refreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(final PullToRefreshBase<ListView> refreshView) {
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
//                        adapter.addAll("is cp");
//                        refreshView.onRefreshComplete();
                        Log.d("out", "1111");
                        refreshAccountsList();
                        refreshListView.onRefreshComplete();
                    }
                }.execute();
            }
        });

        submit.setOnClickListener(this);
//        listView.setOnItemLongClickListener(this);
//        listView.setOnCreateContextMenuListener(this);
        listView = refreshListView.getRefreshableView();
        listView.setOnItemClickListener(this);
        listView.setOnCreateContextMenuListener(this);

    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, DELETE_DATA, 0, "删除数据");
        menu.add(0, UPDATE_DATA, 0, "更新数据");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position - 1;
        Log.d("out","position:"+position);
        switch (item.getItemId()){
            case DELETE_DATA:
                removeAccount(position);
                break;
            case UPDATE_DATA:
                updateAccount(position);
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (!TextUtils.isEmpty(eventsEdit.getText()) && !TextUtils.isEmpty(costEdit.getText())){
            ContentValues values = getMessages(eventsEdit,costEdit);
            add(values);
            refreshAccountsList();
        }else{
            Toast.makeText(this,"内容不能为空",Toast.LENGTH_SHORT).show();
        }
//        refreshAccountsList();
    }

    private void refreshAccountsList(){
        float allCost = 0;
        SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();
        Cursor cursor = db.query("Accounts",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do{
                String event = cursor.getString(cursor.getColumnIndex("event"));
                float cost = cursor.getFloat(cursor.getColumnIndex("cost"));
                allCost += cost;
                int i = 0;
                for(AccountsMessage message : messages){
                    if ((message.getEvents()).equals(event))
                        break;
                    i++;
                }
                if (i == messages.size()){
                    AccountsMessage message = new AccountsMessage();
                    message.setEvents(event);
                    message.setCost(cost);
                    messages.add(message);
                    eventsEdit.setText("");
                    costEdit.setText("");
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
//        listView.setSelection(messages.size());
        showCumulativeCost(allCost);
    }

    private void showCumulativeCost(float cost){
        float costAfterHandler = (float)(Math.round(cost*10))/10;
        allCost.setText(String.valueOf(costAfterHandler));
        if (cost<0){
            allCost.setTextColor(0xff99ff00);
        }else{
            allCost.setTextColor(0xffff0000);
        }
    }

    private void removeAccount(int position) {
        String event = getEvent(position);
        messages.remove(position);
        delete(event);
        refreshAccountsList();
    }

    private String getEvent(int position) {
        AccountsMessage message = messages.get(position);
        return message.getEvents();
    }

    private ContentValues getMessages(EditText eventsEdit,EditText costEdit) {
        String event = eventsEdit.getText().toString();
        float cost = Float.valueOf(costEdit.getText().toString());
        ContentValues values = new ContentValues();
        values.put("event",event);
        values.put("cost",cost);
        return values;
    }

    private void delete(String event){
        SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();
        db.delete("Accounts","event = ?",new String[]{ event });
    }

    private void updateAccount(int position){
        String event = getEvent(position);
        update(event,position);
    }

    private void add(ContentValues values) {
        SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();
        db.insert("Accounts",null,values);
    }

    private void update(final String event,final int position){
        final SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();
        new AlertDialog.Builder(this)
                .setTitle("Please input new message")
                .setView(updateAccountsView)
                .setPositiveButton("Ensure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!TextUtils.isEmpty(updateEventEdit.getText()) && !TextUtils.isEmpty(updateCostEdit.getText())){
                            ContentValues values = getMessages(updateEventEdit,updateCostEdit);
                            db.update("Accounts",values,"event = ?",new String[]{ event });
                            messages.remove(position);
                            refreshAccountsList();
                        }
                    }
                })
                .setNegativeButton("Cancle",null)
                .show();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        AccountsMessage message = messages.get(position-1);
        Intent showDetailIntent = new Intent(this,ShowDetailActivity.class);
        showDetailIntent.putExtra("event",message.getEvents());
        showDetailIntent.putExtra("cost",message.getCost());
        startActivity(showDetailIntent);
    }
}
