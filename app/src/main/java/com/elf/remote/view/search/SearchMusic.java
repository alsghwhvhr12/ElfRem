package com.elf.remote.view.search;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.ActivitySearchMusicBinding;
import com.elf.remote.SearchSong;
import com.elf.remote.model.data.CustomerDataBase;
import com.elf.remote.model.data.CustomerFile;
import com.elf.remote.model.data.DataAdapter;
import com.elf.remote.model.data.LoveDataBase;
import com.elf.remote.model.data.LoveFile;
import com.elf.remote.model.data.MyResvDataBase;
import com.elf.remote.model.data.MySlove;
import com.elf.remote.model.data.MySong;
import com.elf.remote.model.data.VerSionMachin;
import com.elf.remote.utils.RecycleUtils;
import com.elf.remote.view.adapter.CustomerAdapter;
import com.elf.remote.view.adapter.LoveAdapter;
import com.elf.remote.view.adapter.MySLoveAdapter;
import com.elf.remote.view.adapter.MySongAdapter;
import com.elf.remote.view.remotecon.RemoteController;
import com.elf.remote.viewmodel.search.SearchMusicViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class SearchMusic extends AppCompatActivity implements SearchSong {
    ActivitySearchMusicBinding binding;
    int record = 0;
    int i = 0;
    int id = 0;
    int topKind = 1;
    int country = 0;

    String kind = "Idx", preText = "";
    List<MySong> mySongList;
    public List<MySlove> myResvList;
    MySongAdapter mySongAdapter;
    List<MySong> loveList;

    public MySLoveAdapter myResvAdapter;
    MyResvDataBase myResvDataBase;
    Context context;

    public List<LoveFile> loveFiles;
    List<LoveFile> loveTemp;
    public List<CustomerFile> customerFiles;
    List<CustomerFile> customerTemp;

    public LoveAdapter loveAdapter;
    LoveDataBase loveDataBase;

    public CustomerAdapter customerAdapter;
    CustomerDataBase customerDataBase;

    boolean lastItemView = false;

    AudioManager mAudioManager;

    File file;

    String searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_music);
        binding.setViewModel(new SearchMusicViewModel(this));
        binding.executePendingBindings();
        binding.songEdit.setText("");
        customerDataBase = CustomerDataBase.getInstance(this);
        myResvDataBase = MyResvDataBase.getInstance(this);
        file = new File(getApplicationInfo().dataDir + "/databases/sqlitedb.db");

        Intent intent = getIntent();
        record = intent.getIntExtra("record", 0);

        context = this;

        if (VerSionMachin.getName().equals("919")) {
            binding.SendBtn.setVisibility(View.VISIBLE);
        } else {
            binding.SendBtn.setVisibility(View.INVISIBLE);
        }

        if (record == 1) {
            binding.loveBtn.setVisibility(View.GONE);
            binding.BottomSrc.setVisibility(View.GONE);
        }

        Runnable sr = new searchRunnable();

        binding.songEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (file.length() > 0) {
                    searchText = binding.songEdit.getText().toString();
                    i = 0;
                    if (!searchText.contains("ㆍ")) {
                        if (!kind.equals("Singer") && !searchText.equals(preText) && !searchText.startsWith(" ")) {
                            preText = searchText;

                            Thread searchThread = new Thread(sr);
                            searchThread.start();
                        }
                    }
                } else {
                    dialog();
                }
            }
        });

        binding.SongList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemView) {
                    if (topKind == 1) {
                        if (mySongList.size() >= 20) {
                            scrollLoadDB(preText, mySongList.size());
                            mySongAdapter.notifyDataSetChanged();
                        }
                    } else if (topKind == 2) {
                        if (loveFiles.size() >= loveAdapter.count) {
                            loveAdapter.count += 20;
                            if (loveAdapter.count > loveFiles.size()) {
                                int s = loveAdapter.count - loveFiles.size();
                                loveAdapter.count -= s;
                            }
                            loveAdapter.notifyDataSetChanged();
                        }
                    } else if (topKind == 3) {
                        if (customerFiles.size() >= customerAdapter.count) {
                            customerAdapter.count += 20;
                            if (customerAdapter.count > customerFiles.size()) {
                                int s = customerAdapter.count - customerFiles.size();
                                customerAdapter.count -= s;
                            }
                            customerAdapter.notifyDataSetChanged();
                        }
                    } else {
                        if (myResvList.size() >= myResvAdapter.count) {
                            myResvAdapter.count += 20;
                            if (myResvAdapter.count > myResvList.size()) {
                                int s = myResvAdapter.count - myResvList.size();
                                myResvAdapter.count -= s;
                            }
                            myResvAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastItemView = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            }
        });

        binding.SongList.setOnItemClickListener((adapterView, view, i, l) -> {
            if (binding.SongList.getChoiceMode() != AbsListView.CHOICE_MODE_MULTIPLE) {
                if (this.i == 0) {
                    if (topKind == 1) {
                        SungokFragment sun = SungokFragment.getInstance();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("song", mySongList.get(i));
                        bundle.putInt("record", record);
                        sun.setArguments(bundle);
                        sun.show(getSupportFragmentManager(), "sungok");
                    } else {
                        SubSungokFragment sub = SubSungokFragment.getInstance();
                        Bundle bundle = new Bundle();
                        if (topKind == 2) {
                            bundle.putInt("kind", 1);
                            bundle.putInt("touch", i);
                            bundle.putSerializable("song", loveFiles.get(i));
                        } else if (topKind == 3) {
                            bundle.putInt("kind", 3);
                            bundle.putInt("touch", i);
                            bundle.putSerializable("song", customerFiles.get(i));
                        } else {
                            bundle.putInt("kind", 2);
                            bundle.putInt("touch", i);
                            bundle.putSerializable("song", myResvList.get(i));
                        }
                        bundle.putInt("record", record);

                        sub.setArguments(bundle);
                        sub.show(getSupportFragmentManager(), "subgok");
                        sub.setDialogR((where, id) -> {
                            if (id == 2) {
                                loveDataBase.delete(where, null);
                                loveFiles.remove(i);
                                loveAdapter.setChecked(-1);
                                loveAdapter.notifyDataSetChanged();
                            } else if (id == 3) {
                                customerDataBase.delete(where, null);
                                customerFiles.remove(i);
                                customerAdapter.setChecked(-1);
                                customerAdapter.notifyDataSetChanged();
                            } else {
                                myResvDataBase.delete(where, null);
                                myResvList.remove(i);
                                myResvAdapter.setChecked(-1);
                                myResvAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                } else {
                    binding.SongList.setItemChecked(i, true);
                    if (topKind == 2) {
                        loveAdapter.setChecked(i);
                        loveAdapter.notifyDataSetChanged();
                    } else if (topKind == 3) {
                        customerAdapter.setChecked(i);
                        customerAdapter.notifyDataSetChanged();
                    } else {
                        myResvAdapter.setChecked(i);
                        myResvAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "제목을 말하세요.");

        Intent finalIntent = intent;
        binding.stt.setOnClickListener(v -> GoogleStt.launch(finalIntent));

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if (!mAudioManager.isStreamMute(AudioManager.STREAM_MUSIC)) {
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
        }

        binding.SendBtn.setOnClickListener(v -> {
            int count, checked, GrID;
            count = binding.SongList.getCount();
            ArrayList<Integer> GrIds = new ArrayList<>();
            SparseBooleanArray sp;
            String loveKind;

            if (binding.SongList.getChoiceMode() == AbsListView.CHOICE_MODE_SINGLE) {
                if (count > 0) {
                    checked = binding.SongList.getCheckedItemPosition();
                    if (checked > -1) {
                        if (topKind == 2) {
                            GrID = loveFiles.get(checked).getId();
                            loveKind = "myLove";
                        } else {
                            GrID = customerFiles.get(checked).getId();
                            loveKind = "youLove";
                        }

                        TimeSetFragment ts = TimeSetFragment.getInstance();
                        Bundle bundle1 = new Bundle();
                        bundle1.putInt("kind", 3);
                        bundle1.putInt("GroupId", GrID);
                        bundle1.putString("loveKind", loveKind);
                        ts.setArguments(bundle1);
                        ts.show(getSupportFragmentManager(), "timeSet");
                    } else {
                        Toast.makeText(this, "목록을 선택 후 버튼을 클릭해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                sp = binding.SongList.getCheckedItemPositions();
                if (sp.size() > 0) {
                    int i = count - 1;
                    while (i >= 0) {
                        if (sp.get(i)) {
                            if (topKind == 2)
                                GrIds.add(loveFiles.get(i).getId());
                            else GrIds.add(customerFiles.get(i).getId());
                        }
                        i--;
                    }

                    if (topKind == 2) loveKind = "myLove";
                    else loveKind = "youLove";

                    if (GrIds.size() > 0) {
                        TimeSetFragment ts = TimeSetFragment.getInstance();
                        Bundle bundle1 = new Bundle();
                        bundle1.putInt("kind", 3);
                        bundle1.putIntegerArrayList("GroupIds", GrIds);
                        bundle1.putString("loveKind", loveKind);
                        ts.setArguments(bundle1);
                        ts.show(getSupportFragmentManager(), "timeSet");
                    }
                } else {
                    Toast.makeText(this, "목록을 선택 후 버튼을 클릭해 주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.SongList.setOnItemLongClickListener((parent, view, position, id) -> {
            if (binding.SongList.getChoiceMode() != AbsListView.CHOICE_MODE_MULTIPLE) {
                if (topKind == 2 || topKind == 3) {
                    binding.SongList.clearChoices();
                    binding.SongList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
                    binding.SongList.setSelector(R.drawable.list_sel2);
                    binding.SongList.setItemChecked(position, true);
                    if (topKind == 2) {
                        loveAdapter.isChk(View.VISIBLE);
                        loveAdapter.setChecked(-1);
                        loveAdapter.notifyDataSetChanged();
                    } else if (topKind == 3) {
                        customerAdapter.isChk(View.VISIBLE);
                        customerAdapter.setChecked(-1);
                        customerAdapter.notifyDataSetChanged();
                    }
                }
            } else if (binding.SongList.getChoiceMode() == AbsListView.CHOICE_MODE_MULTIPLE) {
                if (topKind == 2 || topKind == 3) {
                    binding.SongList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                    binding.SongList.setSelector(R.drawable.list_sel);
                    if (topKind == 2) {
                        loveAdapter.isChk(View.GONE);
                        loveAdapter.notifyDataSetChanged();
                    } else if (topKind == 3) {
                        customerAdapter.isChk(View.GONE);
                        customerAdapter.notifyDataSetChanged();
                    }
                    binding.SongList.clearChoices();
                }
            }

            return true;
        });
    }

    ActivityResultLauncher<Intent> GoogleStt = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getData() != null) {
                ArrayList<String> matches = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                for (int i = 0; i < matches.size(); i++) {
                    if (!matches.get(i).contains(" ")) binding.songEdit.setText(matches.get(i));
                }
            }
        }
    });

    @Override
    protected void onDestroy() {
        RecycleUtils.recursiveRecycle(getWindow().getDecorView());

        super.onDestroy();
    }

    @Override
    public void remotCall() {
        Intent intent = new Intent(SearchMusic.this, RemoteController.class);
        intent.putExtra("btnOn", 1);
        startActivity(intent);
    }

    @Override
    public void sloCall() {
        i = 0;
        if (!binding.slo.isSelected()) {
            binding.slo.setSelected(true);
            loveDataBase = LoveDataBase.getInstance(this, "MySong");

            getMyLoveData(0);
            loveAdapter = new LoveAdapter(getApplicationContext(), loveFiles);
            binding.SongList.setAdapter(loveAdapter);
            binding.SongList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

            if (binding.resv.isSelected()) {
                binding.resv.setSelected(false);
            } else if (binding.cus.isSelected()) {
                binding.cus.setSelected(false);
            }

            SloFragment slo = SloFragment.getInstance();
            Bundle bundle = new Bundle();
            bundle.putString("LoveKind", "myLove");
            slo.setArguments(bundle);
            slo.show(getSupportFragmentManager(), "slove");
            binding.RegBtn.setBackgroundResource(R.drawable.click_sr_sustreg);
            binding.btnBot.setEnabled(false);
            binding.btnDown.setEnabled(false);
            binding.btnTop.setEnabled(false);
            binding.btnUp.setEnabled(false);
            binding.btnEdit.setBackgroundResource(R.drawable.click_sr_suedit);

            slo.Idcol(id -> {
                getMyLoveData(id);
                this.id = id;
                loveAdapter = new LoveAdapter(getApplicationContext(), loveFiles);
                loveAdapter.setBinding(binding);
                binding.SongList.setAdapter(loveAdapter);
                loveAdapter.notifyDataSetChanged();
                if (record != 1) binding.loveBtn.setVisibility(View.VISIBLE);
                topKind = 2;
                slo.dismiss();
            });
        } else {
            binding.slo.setSelected(false);
            binding.loveBtn.setVisibility(View.GONE);
            binding.SongList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            if (mySongList != null) {
                binding.SongList.setAdapter(mySongAdapter);
                mySongAdapter.notifyDataSetChanged();
            } else {
                loveFiles.clear();
                loveAdapter.notifyDataSetChanged();
            }
            topKind = 1;
        }
    }

    @Override
    public void scuCall() {
        i = 0;
        if (!binding.cus.isSelected()) {
            binding.cus.setSelected(true);

            getMyCustomer(0);
            customerAdapter = new CustomerAdapter(getApplicationContext(), customerFiles);
            binding.SongList.setAdapter(customerAdapter);
            binding.SongList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

            if (binding.slo.isSelected()) {
                binding.slo.setSelected(false);
            } else if (binding.resv.isSelected()) {
                binding.resv.setSelected(false);
            }

            SloFragment slo = SloFragment.getInstance();
            Bundle bundle = new Bundle();
            bundle.putString("LoveKind", "youLove");
            slo.setArguments(bundle);
            slo.show(getSupportFragmentManager(), "slove");
            binding.RegBtn.setBackgroundResource(R.drawable.click_sr_sustreg);
            binding.btnBot.setEnabled(false);
            binding.btnDown.setEnabled(false);
            binding.btnTop.setEnabled(false);
            binding.btnUp.setEnabled(false);
            binding.btnEdit.setBackgroundResource(R.drawable.click_sr_suedit);

            slo.Idcol(id -> {
                getMyCustomer(id);
                this.id = id;
                customerAdapter = new CustomerAdapter(getApplicationContext(), customerFiles);
                customerAdapter.setBinding(binding);
                binding.SongList.setAdapter(customerAdapter);
                customerAdapter.notifyDataSetChanged();
                if (record != 1) binding.loveBtn.setVisibility(View.VISIBLE);
                topKind = 3;
                slo.dismiss();
            });
        } else {
            binding.cus.setSelected(false);
            binding.loveBtn.setVisibility(View.GONE);
            binding.SongList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            if (mySongList != null) {
                binding.SongList.setAdapter(mySongAdapter);
                mySongAdapter.notifyDataSetChanged();
            } else {
                customerFiles.clear();
                customerAdapter.notifyDataSetChanged();
            }
            topKind = 1;
        }
    }

    @Override
    public void srlCall() {
        topKind = 4;
        i = 0;

        if (!binding.resv.isSelected()) {
            binding.SongList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            binding.resv.setSelected(true);
            binding.slo.setSelected(false);
            binding.cus.setSelected(false);
            binding.loveBtn.setVisibility(View.GONE);
            getMyResvData();
            myResvAdapter = new MySLoveAdapter(getApplicationContext(), myResvList);
            binding.SongList.setAdapter(myResvAdapter);
            myResvAdapter.notifyDataSetChanged();
        } else {
            binding.SongList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            binding.resv.setSelected(false);
            if (mySongList != null) {
                binding.SongList.setAdapter(mySongAdapter);
                mySongAdapter.notifyDataSetChanged();
            } else {
                myResvList.clear();
                myResvAdapter.notifyDataSetChanged();
            }
            topKind = 1;
        }
    }

    @Override
    public void cKindCall() {
        CKindFragment ck = CKindFragment.getInstance();
        ck.show(getSupportFragmentManager(), "cKind");

        ck.setCkind(ckind -> {
            switch (ckind) {
                case 1:
                    binding.cKindBtn.setBackgroundResource(R.drawable.click_sr_mctk);
                    if (!binding.songEdit.getText().toString().equals(""))
                        binding.songEdit.setText("");
                    country = 0;
                    break;
                case 2:
                    binding.cKindBtn.setBackgroundResource(R.drawable.click_sr_cpop);
                    if (!binding.songEdit.getText().toString().equals(""))
                        binding.songEdit.setText("");
                    country = 3;
                    break;
                case 3:
                    binding.cKindBtn.setBackgroundResource(R.drawable.click_sr_cchin);
                    if (!binding.songEdit.getText().toString().equals(""))
                        binding.songEdit.setText("");
                    country = 1;
                    break;
                case 4:
                    binding.cKindBtn.setBackgroundResource(R.drawable.click_sr_cjap);
                    if (!binding.songEdit.getText().toString().equals(""))
                        binding.songEdit.setText("");
                    country = 2;
                    break;
                case 5:
                    binding.cKindBtn.setBackgroundResource(R.drawable.click_sr_cchan);
                    if (!binding.songEdit.getText().toString().equals(""))
                        binding.songEdit.setText("");
                    country = 9;
                    break;
                case 6:
                    binding.cKindBtn.setBackgroundResource(R.drawable.click_sr_cgatol);
                    if (!binding.songEdit.getText().toString().equals(""))
                        binding.songEdit.setText("");
                    country = 9;
                    break;
            }
        });
    }

    @Override
    public void jKindCall() {
        JKindFragment jk = JKindFragment.getInstance();
        jk.show(getSupportFragmentManager(), "jKind");

        jk.setJkind(jkind -> {
            switch (jkind) {
                case 1:
                    binding.jkindBtn.setBackgroundResource(R.drawable.click_sr_jae);
                    if (!binding.songEdit.getText().toString().equals(""))
                        binding.songEdit.setText("");
                    kind = "Idx";
                    break;
                case 2:
                    binding.jkindBtn.setBackgroundResource(R.drawable.click_sr_num);
                    if (!binding.songEdit.getText().toString().equals(""))
                        binding.songEdit.setText("");
                    kind = "Number";
                    break;
                case 3:
                    binding.jkindBtn.setBackgroundResource(R.drawable.click_sr_singer);
                    if (!binding.songEdit.getText().toString().equals(""))
                        binding.songEdit.setText("");
                    kind = "Singer";
                    break;
            }
        });
    }

    @Override
    public void addSetCall() {
    }

    @Override
    public void searchCall() {
        if (file.exists()) {
            if (!binding.songEdit.getText().toString().equals("") && kind.equals("Singer")) {
                String text = binding.songEdit.getText().toString();
                SingerListFragment sl = SingerListFragment.getInstance();
                Bundle bundle = new Bundle();
                bundle.putString("singer", text);
                bundle.putString("kind", "Idx");
                bundle.putInt("country", country);
                sl.setArguments(bundle);
                sl.show(getSupportFragmentManager(), "singer");

                sl.SingerIDcol(id -> {
                    initLoadDB(id);
                    mySongAdapter = new MySongAdapter(getApplicationContext(), mySongList, 1);
                    binding.SongList.setAdapter(mySongAdapter);
                    binding.loveBtn.setVisibility(View.GONE);
                });
            }
        } else {
            dialog();
        }
    }

    @Override
    public void upCall() {
        if (loveFiles != null || customerFiles != null) {
            int checked = binding.SongList.getCheckedItemPosition();
            if (topKind == 2) {
                if (checked > 0) {
                    LoveFile checkList = loveFiles.get(checked);
                    LoveFile chgList = loveFiles.get(checked - 1);

                    int x = checkList.getId();
                    checkList.setId(chgList.getId());
                    chgList.setId(x);

                    loveFiles.set(checked, chgList);
                    loveFiles.set(checked - 1, checkList);
                    binding.SongList.setItemChecked(checked - 1, true);
                    binding.SongList.smoothScrollToPosition(checked - 1);

                    loveAdapter.setChecked(checked - 1);
                    loveAdapter.notifyDataSetChanged();
                } else if (checked == 0) {
                    Toast.makeText(this, "첫번째 목록입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "목록을 선택하거나 다중선택 모드를 해제하세요.", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (checked > 0) {
                    CustomerFile checkList = customerFiles.get(checked);
                    CustomerFile chgList = customerFiles.get(checked - 1);

                    int x = checkList.getId();
                    checkList.setId(chgList.getId());
                    chgList.setId(x);

                    customerFiles.set(checked, chgList);
                    customerFiles.set(checked - 1, checkList);
                    binding.SongList.setItemChecked(checked - 1, true);
                    binding.SongList.smoothScrollToPosition(checked - 1);

                    customerAdapter.setChecked(checked - 1);
                    customerAdapter.notifyDataSetChanged();
                } else if (checked == 0) {
                    Toast.makeText(this, "첫번째 목록입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "목록을 선택하거나 다중선택 모드를 해제하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void downCall() {
        if (loveFiles != null || customerFiles != null) {
            int checked = binding.SongList.getCheckedItemPosition();
            if (topKind == 2) {
                if (checked + 1 < loveAdapter.getCount() && checked >= 0) {
                    LoveFile checkList = loveFiles.get(checked);
                    LoveFile chgList = loveFiles.get(checked + 1);

                    int x = checkList.getId();
                    checkList.setId(chgList.getId());
                    chgList.setId(x);

                    loveFiles.set(checked, chgList);
                    loveFiles.set(checked + 1, checkList);
                    binding.SongList.setItemChecked(checked + 1, true);
                    binding.SongList.smoothScrollToPosition(checked + 1);

                    loveAdapter.setChecked(checked + 1);
                    loveAdapter.notifyDataSetChanged();
                } else if (checked + 1 >= loveAdapter.getCount()) {
                    Toast.makeText(this, "마지막 목록입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "목록을 선택하거나 다중선택 모드를 해제하세요.", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (checked + 1 < customerAdapter.getCount() && checked >= 0) {
                    CustomerFile checkList = customerFiles.get(checked);
                    CustomerFile chgList = customerFiles.get(checked + 1);

                    int x = checkList.getId();
                    checkList.setId(chgList.getId());
                    chgList.setId(x);

                    customerFiles.set(checked, chgList);
                    customerFiles.set(checked + 1, checkList);
                    binding.SongList.setItemChecked(checked + 1, true);
                    binding.SongList.smoothScrollToPosition(checked + 1);

                    customerAdapter.setChecked(checked + 1);
                    customerAdapter.notifyDataSetChanged();
                } else if (checked + 1 >= loveAdapter.getCount()) {
                    Toast.makeText(this, "마지막 목록입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "목록을 선택하거나 다중선택 모드를 해제하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void topCall() {
        if (loveFiles != null || customerFiles != null) {
            int checked = binding.SongList.getCheckedItemPosition();
            int top = binding.SongList.getHeaderViewsCount();
            if (topKind == 2) {
                if (checked != top && checked >= 0) {
                    LoveFile checkList = loveFiles.get(checked);
                    LoveFile chgList = loveFiles.get(top);

                    int x = chgList.getId();

                    int pos = top + 1;
                    for (LoveFile ignored : loveFiles) {
                        if (pos <= checked) {
                            LoveFile list = loveFiles.get(pos);
                            chgList.setId(list.getId());
                            loveFiles.set(pos, chgList);
                            chgList = list;
                            pos++;
                        }
                    }

                    checkList.setId(x);

                    loveFiles.set(top, checkList);
                    binding.SongList.setItemChecked(top, true);
                    binding.SongList.smoothScrollToPosition(top);

                    loveAdapter.setChecked(top);
                    loveAdapter.notifyDataSetChanged();
                } else if (checked == 0) {
                    Toast.makeText(this, "첫번째 목록입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "목록을 선택하거나 다중선택 모드를 해제하세요.", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (checked != top && checked >= 0) {
                    CustomerFile checkList = customerFiles.get(checked);
                    CustomerFile chgList = customerFiles.get(top);

                    int x = chgList.getId();

                    int pos = top + 1;
                    for (CustomerFile ignored : customerFiles) {
                        if (pos <= checked) {
                            CustomerFile list = customerFiles.get(pos);
                            chgList.setId(list.getId());
                            customerFiles.set(pos, chgList);
                            chgList = list;
                            pos++;
                        }
                    }

                    checkList.setId(x);

                    customerFiles.set(top, checkList);
                    binding.SongList.setItemChecked(top, true);
                    binding.SongList.smoothScrollToPosition(top);

                    customerAdapter.setChecked(top);
                    customerAdapter.notifyDataSetChanged();
                } else if (checked == 0) {
                    Toast.makeText(this, "첫번째 목록입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "목록을 선택하거나 다중선택 모드를 해제하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void bottomCall() {
        if (loveFiles != null || customerFiles != null) {
            int checked = binding.SongList.getCheckedItemPosition();
            if (topKind == 2) {
                if (checked >= 0) {
                    int bot = loveAdapter.getCount() - 1;
                    if (checked != bot) {
                        LoveFile checkList = loveFiles.get(checked);
                        LoveFile chgList = loveFiles.get(bot);

                        int x = chgList.getId();

                        int pos = bot - 1;
                        for (LoveFile ignored : loveFiles) {
                            if (checked <= pos) {
                                LoveFile list = loveFiles.get(pos);
                                chgList.setId(list.getId());
                                loveFiles.set(pos, chgList);
                                chgList = list;
                                pos--;
                            }
                        }

                        checkList.setId(x);

                        loveFiles.set(bot, checkList);
                        binding.SongList.setItemChecked(bot, true);
                        binding.SongList.smoothScrollToPosition(bot);

                        loveAdapter.setChecked(bot);
                        loveAdapter.notifyDataSetChanged();
                    }
                } else if (checked + 1 >= loveAdapter.getCount()) {
                    Toast.makeText(this, "마지막 목록입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "목록을 선택하거나 다중선택 모드를 해제하세요.", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (checked >= 0) {
                    int bot = customerAdapter.getCount() - 1;
                    if (checked != bot) {
                        CustomerFile checkList = customerFiles.get(checked);
                        CustomerFile chgList = customerFiles.get(bot);

                        int x = chgList.getId();

                        int pos = bot - 1;
                        for (CustomerFile ignored : customerFiles) {
                            if (checked <= pos) {
                                CustomerFile list = customerFiles.get(pos);
                                chgList.setId(list.getId());
                                customerFiles.set(pos, chgList);
                                chgList = list;
                                pos--;
                            }
                        }

                        checkList.setId(x);

                        customerFiles.set(bot, checkList);
                        binding.SongList.setItemChecked(bot, true);
                        binding.SongList.smoothScrollToPosition(bot);

                        customerAdapter.setChecked(bot);
                        customerAdapter.notifyDataSetChanged();
                    }
                } else if (checked + 1 >= loveAdapter.getCount()) {
                    Toast.makeText(this, "마지막 목록입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "목록을 선택하거나 다중선택 모드를 해제하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void EditSaveCAll() {
        if (loveFiles != null || customerFiles != null) {
            if (i == 0) {
                i = 1;
                binding.btnBot.setEnabled(true);
                binding.btnDown.setEnabled(true);
                binding.btnTop.setEnabled(true);
                binding.btnUp.setEnabled(true);
                binding.btnEdit.setBackgroundResource(R.drawable.click_sr_susave);
            } else {
                i = 0;
                binding.btnBot.setEnabled(false);
                binding.btnDown.setEnabled(false);
                binding.btnTop.setEnabled(false);
                binding.btnUp.setEnabled(false);
                binding.btnEdit.setBackgroundResource(R.drawable.click_sr_suedit);

                TimeSetFragment ta = TimeSetFragment.getInstance();
                Bundle bundle = new Bundle();
                bundle.putInt("kind", 5);
                ta.setArguments(bundle);
                ta.show(getSupportFragmentManager(), "DIALOG");

                ta.setDialogR(finish -> {
                    if (topKind == 2) {
                        if (finish.equals("1")) {
                            for (int i = 0; i < loveFiles.size(); i++) {
                                if (id == loveFiles.get(i).GroupID) {
                                    ContentValues addRowValue = new ContentValues();
                                    addRowValue.put("GroupID", id);
                                    addRowValue.put("Number", loveFiles.get(i).getNumber());
                                    addRowValue.put("Tempo", loveFiles.get(i).getTempo());
                                    addRowValue.put("PlayKey", loveFiles.get(i).getPlayKey());
                                    String where = "rowid = " + loveFiles.get(i).getId();
                                    loveDataBase.update(addRowValue, where, null);
                                    loveTemp.clear();
                                    loveTemp.addAll(loveFiles);
                                }
                            }
                        } else {
                            loveFiles.clear();
                            loveFiles.addAll(loveTemp);
                            for (int i = 0; i < loveAdapter.getCount(); i++)
                                loveFiles.get(i).setId(i + 1);
                            binding.SongList.clearChoices();
                        }
                        loveAdapter.setChecked(-1);
                        loveAdapter.notifyDataSetChanged();
                    } else {
                        if (finish.equals("1")) {
                            for (int i = 0; i < customerFiles.size(); i++) {
                                if (id == customerFiles.get(i).CustomerID) {
                                    ContentValues addRowValue = new ContentValues();
                                    addRowValue.put("CustomerID", id);
                                    addRowValue.put("Number", customerFiles.get(i).getNumber());
                                    addRowValue.put("Tempo", customerFiles.get(i).getTempo());
                                    addRowValue.put("PlayKey", customerFiles.get(i).getPlayKey());
                                    String where = "rowid = " + customerFiles.get(i).getId();
                                    customerDataBase.update(addRowValue, where, null);
                                    customerTemp.clear();
                                    customerTemp.addAll(customerFiles);
                                }
                            }
                        } else {
                            customerFiles.clear();
                            customerFiles.addAll(customerTemp);
                            for (int i = 0; i < customerAdapter.getCount(); i++)
                                customerFiles.get(i).setId(i + 1);
                            binding.SongList.clearChoices();
                        }
                        customerAdapter.setChecked(-1);
                        customerAdapter.notifyDataSetChanged();
                    }

                    if (binding.SongList.getChoiceMode() == AbsListView.CHOICE_MODE_MULTIPLE) {
                        binding.SongList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                        binding.SongList.setSelector(R.drawable.list_sel);
                        if (topKind == 2) {
                            loveAdapter.isChk(View.GONE);
                            loveAdapter.notifyDataSetChanged();
                        } else if (topKind == 3) {
                            customerAdapter.isChk(View.GONE);
                            customerAdapter.notifyDataSetChanged();
                        }
                        binding.SongList.clearChoices();
                    }
                });
            }
        }
    }

    @Override
    public void sortCall() {
        if (loveFiles != null || customerFiles != null) {
            SortListFragment sl = SortListFragment.getInstance();
            sl.show(getSupportFragmentManager(), "sortList");

            sl.setSortList(kind -> {
                switch (kind) {
                    case 1:
                        binding.RegBtn.setBackgroundResource(R.drawable.click_sr_sustjaea);
                        if (topKind == 2) {
                            Comparator<LoveFile> titleASC = (s, t1) -> s.getTitle().compareTo(t1.getTitle());
                            loveFiles.sort(titleASC);
                            loveAdapter.setChecked(-1);
                            loveAdapter.notifyDataSetChanged();
                        } else if (topKind == 3) {
                            Comparator<CustomerFile> titleASC = (s, t1) -> s.getTitle().compareTo(t1.getTitle());
                            customerFiles.sort(titleASC);
                            customerAdapter.setChecked(-1);
                            customerAdapter.notifyDataSetChanged();
                        }
                        break;
                    case 2:
                        binding.RegBtn.setBackgroundResource(R.drawable.click_sr_sustreg);
                        if (topKind == 2) {
                            Comparator<LoveFile> id = (s, t1) -> (s.getId() - t1.getId());
                            loveFiles.sort(id);
                            loveAdapter.setChecked(-1);
                            loveAdapter.notifyDataSetChanged();
                        } else if (topKind == 3) {
                            Comparator<CustomerFile> id = (s, t1) -> (s.getId() - t1.getId());
                            customerFiles.sort(id);
                            customerAdapter.setChecked(-1);
                            customerAdapter.notifyDataSetChanged();
                        }
                        break;
                    case 3:
                        binding.RegBtn.setBackgroundResource(R.drawable.click_sr_sustsinga);
                        if (topKind == 2) {
                            Comparator<LoveFile> singASC = (s, t1) -> s.getSinger().compareTo(t1.getSinger());
                            loveFiles.sort(singASC);
                            loveAdapter.setChecked(-1);
                            loveAdapter.notifyDataSetChanged();
                        } else if (topKind == 3) {
                            Comparator<CustomerFile> singASC = (s, t1) -> s.getSinger().compareTo(t1.getSinger());
                            customerFiles.sort(singASC);
                            customerAdapter.setChecked(-1);
                            customerAdapter.notifyDataSetChanged();
                        }
                        break;
                    case 4:
                        binding.RegBtn.setBackgroundResource(R.drawable.click_sr_sustjaed);
                        if (topKind == 2) {
                            Comparator<LoveFile> titleDESC = (s, t1) -> t1.getTitle().compareTo(s.getTitle());
                            loveFiles.sort(titleDESC);
                            loveAdapter.setChecked(-1);
                            loveAdapter.notifyDataSetChanged();
                        } else if (topKind == 3) {
                            Comparator<CustomerFile> titleDESC = (s, t1) -> t1.getTitle().compareTo(s.getTitle());
                            customerFiles.sort(titleDESC);
                            customerAdapter.setChecked(-1);
                            customerAdapter.notifyDataSetChanged();
                        }
                        break;
                    case 5:
                        binding.RegBtn.setBackgroundResource(R.drawable.click_sr_sustsingd);
                        if (topKind == 2) {
                            Comparator<LoveFile> singDESC = (s, t1) -> t1.getSinger().compareTo(s.getSinger());
                            loveFiles.sort(singDESC);
                            loveAdapter.setChecked(-1);
                            loveAdapter.notifyDataSetChanged();
                        } else if (topKind == 3) {
                            Comparator<CustomerFile> singDESC = (s, t1) -> t1.getSinger().compareTo(s.getSinger());
                            customerFiles.sort(singDESC);
                            customerAdapter.setChecked(-1);
                            customerAdapter.notifyDataSetChanged();
                        }
                        break;
                }
            });
        }
    }

    public void getMyLoveData(int id) {
        loveFiles = new ArrayList<>();
        loveTemp = new ArrayList<>();

        String[] columns = new String[]{"rowid", "GroupID", "Number", "Tempo", "PlayKey"};
        String selection = "GroupID = " + id;
        String order = "rowid ASC";

        Cursor cursor = loveDataBase.query(columns, selection, null, null, null, order);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                LoveFile LoveData = new LoveFile();

                LoveData.setId(cursor.getInt(0));
                LoveData.setGroupID(cursor.getInt(1));
                LoveData.setNumber(cursor.getInt(2));
                LoveData.setTempo(cursor.getInt(3));
                LoveData.setPlayKey(cursor.getInt(4));

                getLoveSong(cursor.getInt(2));
                LoveData.setSinger(loveList.get(0).Singer);
                LoveData.setTitle(loveList.get(0).Title);

                loveFiles.add(LoveData);
                loveTemp.add(LoveData);
            }
        }
    }

    public void getMyCustomer(int id) {
        customerFiles = new ArrayList<>();
        customerTemp = new ArrayList<>();

        String[] columns = new String[]{"rowid", "CustomerID", "Number", "Tempo", "PlayKey"};
        String selection = "CustomerID = " + id;
        String order = "rowid ASC";

        Cursor cursor = customerDataBase.query(columns, selection, null, null, null, order);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                CustomerFile LoveData = new CustomerFile();

                LoveData.setId(cursor.getInt(0));
                LoveData.setCustomerID(cursor.getInt(1));
                LoveData.setNumber(cursor.getInt(2));
                LoveData.setTempo(cursor.getInt(3));
                LoveData.setPlayKey(cursor.getInt(4));

                getLoveSong(cursor.getInt(2));
                LoveData.setSinger(loveList.get(0).Singer);
                LoveData.setTitle(loveList.get(0).Title);
                customerFiles.add(LoveData);
                customerTemp.add(LoveData);
            }
        }
    }

    public void getMyResvData() {
        myResvList = new ArrayList<>();

        String[] columns = new String[]{"_id", "number", "title", "singer", "tempo", "interval", "tempo2", "interval2", "count", "AbsMain"};

        Cursor cursor = myResvDataBase.query(columns, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                MySlove LoveData = new MySlove();

                LoveData.setId(cursor.getInt(0));
                LoveData.setNumber(cursor.getInt(1));
                LoveData.setTitle(cursor.getString(2));
                LoveData.setSinger(cursor.getString(3));
                LoveData.setTempo(cursor.getInt(4));
                LoveData.setMain(cursor.getString(5));
                LoveData.setTmep(cursor.getInt(6));
                LoveData.setIntaval(cursor.getString(7));
                LoveData.setCount(cursor.getInt(8));
                LoveData.setAbsMain(cursor.getInt(9));

                myResvList.add(LoveData);
            }
        }
    }

    private void initLoadDB(String text) {
        DataAdapter mDbHelper = new DataAdapter(getApplicationContext());
        mDbHelper.open();
        // db에 있는 값들을 model을 적용해서 넣는다.
        mySongList = mDbHelper.getTableData(text, kind, country);
        // db 닫기
        mDbHelper.close();
    }

    private void scrollLoadDB(String text, int min) {
        DataAdapter mDbHelper = new DataAdapter(getApplicationContext());
        mDbHelper.open();
        // db에 있는 값들을 model을 적용해서 넣는다.
        mySongList = mDbHelper.getScrollData(text, kind, country, min, mySongList);
        // db 닫기
        mDbHelper.close();
    }

    private void getLoveSong(int text) {
        DataAdapter mDbHelper = new DataAdapter(context.getApplicationContext());
        mDbHelper.open();
        // db에 있는 값들을 model을 적용해서 넣는다.
        loveList = mDbHelper.getCustomer(text);
        // db 닫기
        mDbHelper.close();
    }

    public void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("곡 목록이 없습니다. \n 데이터 네트워크를 연결 후 앱을 재실행해 주세요.");
        builder.setNegativeButton("확인",
                (dialog, which) -> {
                });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        if (binding.SongList.getChoiceMode() == AbsListView.CHOICE_MODE_MULTIPLE) {
            binding.SongList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            binding.SongList.setSelector(R.drawable.list_sel);
            if (topKind == 2) {
                loveAdapter.isChk(View.GONE);
                loveAdapter.notifyDataSetChanged();
            } else if (topKind == 3) {
                customerAdapter.isChk(View.GONE);
                customerAdapter.notifyDataSetChanged();
            }
            binding.SongList.clearChoices();
        } else {
            finish();
        }
    }

    class searchRunnable implements Runnable {
        @Override
        public void run() {
            synchronized (this) {
                if (!searchText.equals("") && !searchText.contains("'")) {
                    initLoadDB(searchText);
                    mySongAdapter = new MySongAdapter(getApplicationContext(), mySongList, 1);
                }
                mySongAdapter.filter(searchText);
                runOnUiThread(() -> {
                    binding.SongList.setAdapter(mySongAdapter);
                    binding.loveBtn.setVisibility(View.GONE);
                    binding.resv.setSelected(false);
                    binding.slo.setSelected(false);
                    binding.cus.setSelected(false);
                });
                topKind = 1;
            }
        }
    }
}