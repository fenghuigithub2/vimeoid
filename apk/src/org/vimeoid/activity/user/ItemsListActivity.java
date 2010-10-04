package org.vimeoid.activity.user;

import org.json.JSONException;
import org.json.JSONObject;
import org.vimeoid.R;
import org.vimeoid.activity.base.ApiTask_;
import org.vimeoid.activity.base.ItemsListActivity_;
import org.vimeoid.activity.base.ListApiTask_.Reactor;
import org.vimeoid.adapter.JsonObjectsAdapter;
import org.vimeoid.util.AdvancedItem;
import org.vimeoid.util.ApiParams;
import org.vimeoid.util.Dialogs;
import org.vimeoid.util.Invoke;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public abstract class ItemsListActivity<ItemType extends AdvancedItem> extends 
                      ItemsListActivity_<ItemType, JsonObjectsAdapter<ItemType>> {
	
    public static final String TAG = "ItemsListActivity";
    
    private String apiMethod;
    private ApiParams params;
    
    private ListApiTask mainTask = null;
    private boolean needMorePages = true;
    
    protected final ApiTasksQueue secondaryTasks;
    
    private final Reactor<JSONObject> mainReactor;

    public ItemsListActivity(int mainView, int contextMenu) {
    	super(mainView, contextMenu);
    	
    	secondaryTasks = new ApiTasksQueue() {            
            @Override public void onPerfomed(int taskId, JSONObject result) throws JSONException {
                onSecondaryTaskPerfomed(taskId, result);
            }

            @Override public void onError(Exception e, String message) {
                Log.e(TAG, message + " / " + e.getLocalizedMessage());
                Dialogs.makeExceptionToast(ItemsListActivity.this, message, e);
            }
        };
        
        mainReactor = new ListReactor<JSONObject>() {
            
            @Override
            public boolean afterRequest(JSONObject result, int received,
                    boolean needMore, ApiTask_<?, JSONObject> nextPageTask) {
                
                onContentChanged();
                
                needMorePages = needMore;
                mainTask = (ListApiTask)nextPageTask;                
                
                return super.afterRequest(result, received, needMore, nextPageTask);

            }
            
        };
    }
    
    public ItemsListActivity(int contextMenu) {
    	this(R.layout.generic_list, contextMenu);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        apiMethod = getIntent().getStringExtra(Invoke.Extras.API_METHOD);
        params = ApiParams.fromBundle(getIntent().getBundleExtra(Invoke.Extras.API_PARAMS));        
        
        super.onCreate(savedInstanceState);
    }
    
    protected void initTitleBar(ImageView subjectIcon, TextView subjectTitle, ImageView resultIcon) {
        subjectIcon.setImageResource(getIntent().getIntExtra(Invoke.Extras.SUBJ_ICON, R.drawable.info));
        subjectTitle.setText(getIntent().hasExtra(Invoke.Extras.SUBJ_TITLE) 
                             ? getIntent().getStringExtra(Invoke.Extras.SUBJ_TITLE) 
                             : getString(R.string.unknown));
        resultIcon.setImageResource(getIntent().getIntExtra(Invoke.Extras.RES_ICON, R.drawable.info));
    }
    
    @Override
    protected final void loadNextPage(JsonObjectsAdapter<ItemType> adapter) {
    	if (!needMorePages) return;
        if (mainTask == null) {
            mainTask = new ListApiTask(mainReactor, adapter, apiMethod);
            mainTask.setMaxPages(10);
            mainTask.setPerPage(20);
        }
        mainTask.execute(params);
    }
        
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater(); //from activity
        inflater.inflate(R.menu.user_options_menu, menu); 
        
        return true;
    }
   
    public void onSecondaryTaskPerfomed(int id, JSONObject result)  throws JSONException { }  
   
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        switch (item.getItemId()) {
            case R.id.menu_Refresh: {
                    Dialogs.makeToast(this, getString(R.string.currently_not_supported)); 
                } break;
            case R.id.menu_Preferences: {
                    Dialogs.makeToast(this, getString(R.string.currently_not_supported)); 
                } break;
            case R.id.menu_SwitchView: {
                    Dialogs.makeToast(this, getString(R.string.currently_not_supported)); 
                } break;
            default: Dialogs.makeToast(this, getString(R.string.unknown_item));
        }         
        return super.onOptionsItemSelected(item);
        
    }
            
}
