package com.elf.remote.view.search;

import android.app.Dialog;
import android.content.ContentValues;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.elf.mvvmremote.R;
import com.elf.mvvmremote.databinding.FragmentMyloveBinding;
import com.elf.remote.SubCall;
import com.elf.remote.model.data.CustomerGroup;
import com.elf.remote.model.data.LoveGroup;
import com.elf.remote.model.data.VerSionMachin;
import com.elf.remote.viewmodel.search.SloViewModel;

import java.util.ArrayList;

public class SloFragment extends DialogFragment implements SubCall {
    getId gt;

    private FragmentMyloveBinding binding;
    SloViewModel model;

    public SloFragment() {
    }

    public static SloFragment getInstance() {
        return new SloFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        model = new SloViewModel(this, bundle, gt);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mylove, container, false);
        binding.setViewModel(model);
        binding.executePendingBindings();
        setCancelable(false);

        model.onCreate();

        if (VerSionMachin.getName().equals("919")) {
            binding.SendBtn.setVisibility(View.VISIBLE);
        } else {
            binding.SendBtn.setVisibility(View.INVISIBLE);
        }

        if (model.loveKind.equals("myLove")) {
            binding.titleLove.setBackgroundResource(R.drawable.glmylv_topbar);
            binding.loveList.setAdapter(model.loveGroupAdapter);
            model.loveGroupAdapter.setBinding(binding);
        } else {
            binding.titleLove.setBackgroundResource(R.drawable.glcuslv_topbar);
            binding.loveList.setAdapter(model.customerGroupAdapter);
            model.customerGroupAdapter.setBinding(binding);
        }

        binding.loveList.setOnItemLongClickListener((parent, view, position, id) -> {
            if (binding.loveList.getChoiceMode() != AbsListView.CHOICE_MODE_MULTIPLE) {
                binding.loveList.clearChoices();
                binding.loveList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
                binding.loveList.setItemChecked(position, true);
                if (model.loveKind.equals("myLove")) {
                    model.loveGroupAdapter.setChecked(-1);
                    model.loveGroupAdapter.isChk(View.VISIBLE);
                    model.loveGroupAdapter.notifyDataSetChanged();
                } else {
                    model.customerGroupAdapter.setChecked(-1);
                    model.customerGroupAdapter.isChk(View.VISIBLE);
                    model.customerGroupAdapter.notifyDataSetChanged();
                }
                model.cl = 1;
            } else if (binding.loveList.getChoiceMode() == AbsListView.CHOICE_MODE_MULTIPLE) {
                binding.loveList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                if (model.loveKind.equals("myLove")) {
                    model.loveGroupAdapter.isChk(View.GONE);
                    model.loveGroupAdapter.notifyDataSetChanged();
                } else {
                    model.customerGroupAdapter.isChk(View.GONE);
                    model.customerGroupAdapter.notifyDataSetChanged();
                }
                binding.loveList.clearChoices();
                model.cl = 0;
            }
            return true;
        });

        binding.SendBtn.setOnClickListener(v -> {
            int count, checked, GrID;
            ArrayList<Integer> GrIds = new ArrayList<>();
            if (model.loveKind.equals("myLove")) count = model.loveGroupAdapter.getCount();
            else count = model.customerGroupAdapter.getCount();
            SparseBooleanArray sp;

            if (count > 0) {
                if (binding.loveList.getChoiceMode() == AbsListView.CHOICE_MODE_SINGLE) {
                    checked = binding.loveList.getCheckedItemPosition();

                    if (checked > -1) {
                        if (model.loveKind.equals("myLove"))
                            GrID = model.loveGroups.get(checked).getGroupId();
                        else GrID = model.customerGroups.get(checked).getCustomerId();

                        TimeSetFragment ts = TimeSetFragment.getInstance();
                        Bundle bundle1 = new Bundle();
                        bundle1.putInt("kind", 2);
                        bundle1.putInt("GroupId", GrID);
                        bundle1.putString("loveKind", model.loveKind);
                        ts.setArguments(bundle1);
                        ts.show(getChildFragmentManager(), "timeSet");
                    }
                } else {
                    sp = binding.loveList.getCheckedItemPositions();
                    if (sp.size() > 0) {
                        int i = count - 1;
                        while (i >= 0) {
                            if (sp.get(i)) {
                                if (model.loveKind.equals("myLove")) GrIds.add(model.loveGroups.get(i).getGroupId());
                                else GrIds.add(model.customerGroups.get(i).getCustomerId());
                            }
                            i--;
                        }
                        if (GrIds.size() > 0) {
                            TimeSetFragment ts = TimeSetFragment.getInstance();
                            Bundle bundle1 = new Bundle();
                            bundle1.putInt("kind", 2);
                            bundle1.putIntegerArrayList("GroupIds", GrIds);
                            bundle1.putString("loveKind", model.loveKind);
                            ts.setArguments(bundle1);
                            ts.show(getChildFragmentManager(), "timeSet");
                        }
                    }
                }
            }
        });

        return binding.getRoot();
    }

    @Override
    public void closeCall() {
        dismiss();
    }

    @Override
    public void oneCall() {
        SloAddFragment sa = SloAddFragment.getInstance();
        Bundle bundle = new Bundle();
        bundle.putString("title", "");
        bundle.putString("memo", "");
        sa.setArguments(bundle);
        sa.show(getChildFragmentManager(), "SloAdd");

        sa.addDialog((title, memo) -> {
            if (!title.equals("")) {
                model.addSlo(title, memo);

                if (model.loveKind.equals("myLove")) {
                    binding.loveList.smoothScrollToPosition(model.loveGroupAdapter.getCount() + 1);
                } else {
                    binding.loveList.smoothScrollToPosition(model.customerGroupAdapter.getCount() + 1);
                }
            }
        });
    }

    @Override
    public void twoCall() {
        int count, checked;
        SparseBooleanArray sp;
        if (model.loveKind.equals("myLove")) {
            count = model.loveGroupAdapter.getCount();

            if (count > 0) {
                if (binding.loveList.getChoiceMode() == AbsListView.CHOICE_MODE_SINGLE) {
                    checked = binding.loveList.getCheckedItemPosition();
                    if (checked > -1 && checked < count) {
                        SloAddFragment sa = SloAddFragment.getInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString("title", model.loveGroups.get(binding.loveList.getCheckedItemPosition()).getName());
                        bundle.putString("memo", model.loveGroups.get(binding.loveList.getCheckedItemPosition()).getMemo());
                        sa.setArguments(bundle);
                        sa.show(getChildFragmentManager(), "SloEdit");

                        sa.addDialog((title, memo) -> {
                            if (!title.equals("")) {
                                String where = "rowid = " + model.loveGroups.get(checked).getId();
                                ContentValues addRowValue = new ContentValues();
                                addRowValue.put("Name", title);
                                addRowValue.put("Memo", memo);
                                model.loveGroupDataBase.update(addRowValue, where, null);

                                LoveGroup data = new LoveGroup();

                                data.setId(model.loveGroups.get(checked).getId());
                                data.setGroupId(model.loveGroups.get(checked).getGroupId());
                                data.setName(title);
                                data.setMemo(memo);

                                model.loveGroups.set(checked, data);
                                model.tempFiles2.set(checked, data);
                                model.loveGroupAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                } else {
                    sp = binding.loveList.getCheckedItemPositions();
                    int i = count - 1;
                    while (i >= 0) {
                        if (sp.get(i)) {
                            SloAddFragment sa = SloAddFragment.getInstance();
                            Bundle bundle = new Bundle();
                            bundle.putString("title", model.loveGroups.get(i).getName());
                            bundle.putString("memo", model.loveGroups.get(i).getMemo());
                            sa.setArguments(bundle);
                            sa.show(getChildFragmentManager(), "SloEdit" + i);

                            int finalI = i;
                            sa.addDialog((title, memo) -> {
                                if (!title.equals("")) {
                                    String where = "rowid = " + model.loveGroups.get(finalI).getId();
                                    ContentValues addRowValue = new ContentValues();
                                    addRowValue.put("Name", title);
                                    addRowValue.put("Memo", memo);
                                    model.loveGroupDataBase.update(addRowValue, where, null);

                                    LoveGroup data = new LoveGroup();

                                    data.setId(model.loveGroups.get(finalI).getId());
                                    data.setGroupId(model.loveGroups.get(finalI).getGroupId());
                                    data.setName(title);
                                    data.setMemo(memo);

                                    model.loveGroups.set(finalI, data);
                                    model.tempFiles2.set(finalI, data);
                                    model.loveGroupAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                        i--;
                    }
                }
            }
        } else {
            count = model.customerGroupAdapter.getCount();

            if (count > 0) {
                if (binding.loveList.getChoiceMode() == AbsListView.CHOICE_MODE_SINGLE) {
                    checked = binding.loveList.getCheckedItemPosition();
                    if (checked > -1 && checked < count) {
                        SloAddFragment sa = SloAddFragment.getInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString("title", model.customerGroups.get(binding.loveList.getCheckedItemPosition()).getName());
                        bundle.putString("memo", model.customerGroups.get(binding.loveList.getCheckedItemPosition()).getMemo());
                        sa.setArguments(bundle);
                        sa.show(getChildFragmentManager(), "SloEdit");

                        sa.addDialog((title, memo) -> {
                            if (!title.equals("")) {
                                String where = "rowid = " + model.customerGroups.get(checked).getId();
                                ContentValues addRowValue = new ContentValues();
                                addRowValue.put("Name", title);
                                addRowValue.put("Memo", memo);
                                model.customerGroupDataBase.update(addRowValue, where, null);

                                CustomerGroup data = new CustomerGroup();

                                data.setId(model.customerGroups.get(checked).getId());
                                data.setCustomerId(model.customerGroups.get(checked).getCustomerId());
                                data.setName(title);
                                data.setMemo(memo);

                                model.customerGroups.set(checked, data);
                                model.tempFiles.set(checked, data);
                                model.customerGroupAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                } else {
                    sp = binding.loveList.getCheckedItemPositions();
                    int i = count - 1;
                    while (i >= 0) {
                        if (sp.get(i)) {
                            SloAddFragment sa = SloAddFragment.getInstance();
                            Bundle bundle = new Bundle();
                            bundle.putString("title", model.customerGroups.get(i).getName());
                            bundle.putString("memo", model.customerGroups.get(i).getMemo());
                            sa.setArguments(bundle);
                            sa.show(getChildFragmentManager(), "SloEdit" + i);

                            int finalI = i;
                            sa.addDialog((title, memo) -> {
                                if (!title.equals("")) {
                                    String where = "rowid = " + model.customerGroups.get(finalI).getId();
                                    ContentValues addRowValue = new ContentValues();
                                    addRowValue.put("Name", title);
                                    addRowValue.put("Memo", memo);
                                    model.customerGroupDataBase.update(addRowValue, where, null);

                                    CustomerGroup data = new CustomerGroup();

                                    data.setId(model.customerGroups.get(finalI).getId());
                                    data.setCustomerId(model.customerGroups.get(finalI).getCustomerId());
                                    data.setName(title);
                                    data.setMemo(memo);

                                    model.customerGroups.set(finalI, data);
                                    model.tempFiles.set(finalI, data);
                                    model.customerGroupAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                        i--;
                    }
                }
            }
        }
        if (binding.loveList.getChoiceMode() == AbsListView.CHOICE_MODE_MULTIPLE) {
            binding.loveList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            if (model.loveKind.equals("myLove")) {
                model.loveGroupAdapter.isChk(View.GONE);
                model.loveGroupAdapter.notifyDataSetChanged();
            } else {
                model.customerGroupAdapter.isChk(View.GONE);
                model.customerGroupAdapter.notifyDataSetChanged();
            }
            binding.loveList.clearChoices();
            model.cl = 0;
        }
    }

    @Override
    public void threeCall() {
        int count, checked = 0;

        if (model.loveKind.equals("myLove")) count = model.loveGroupAdapter.getCount();
        else count = model.customerGroupAdapter.getCount();

        TimeSetFragment ta = TimeSetFragment.getInstance();
        SparseBooleanArray sp = null;

        if (count > 0) {
            checked = binding.loveList.getCheckedItemPosition();
            sp = binding.loveList.getCheckedItemPositions();
            if ((checked > -1 && checked < count) || binding.loveList.getChoiceMode() == AbsListView.CHOICE_MODE_MULTIPLE) {
                Bundle bundle = new Bundle();
                bundle.putInt("kind", 4);
                ta.setArguments(bundle);
                ta.show(getChildFragmentManager(), "DIALOG");
            } else {
                Toast.makeText(getContext(), "삭제할 목록을 선택하세요.", Toast.LENGTH_SHORT).show();
            }
        }

        int finalChecked = checked;
        SparseBooleanArray finalSp = sp;
        ta.setDialogR(finish -> {
            String where, where2;

            if (model.loveKind.equals("myLove")) {
                if (binding.loveList.getChoiceMode() == AbsListView.CHOICE_MODE_SINGLE) {
                    where = finish + '"' + model.loveGroups.get(finalChecked).getId() + '"';
                    where2 = "GroupID = " + model.loveGroups.get(finalChecked).getGroupId();
                    model.loveGroupDataBase.delete(where, null);
                    model.loveDataBase.delete(where2, null);
                    model.loveGroups.remove(finalChecked);
                    model.tempFiles2.remove(finalChecked);
                } else {
                    for (int i = count - 1; i >= 0; i--) {
                        if (finalSp.get(i)) {
                            where = finish + '"' + model.loveGroups.get(i).getId() + '"';
                            where2 = "GroupID = " + model.loveGroups.get(i).getGroupId();
                            model.loveGroupDataBase.delete(where, null);
                            model.loveDataBase.delete(where2, null);
                            model.loveGroups.remove(i);
                            model.tempFiles2.remove(i);
                        }
                    }
                }
                binding.loveList.clearChoices();

                for (int i = 0; i < model.loveGroupAdapter.getCount(); i++) {
                    model.loveGroups.get(i).setId(i + 1);
                    String Gwhere = "GroupID = " + model.loveGroups.get(i).getGroupId();
                    ContentValues addRowValue = new ContentValues();
                    addRowValue.put("GroupID", i + 1);
                    model.loveGroupDataBase.update(addRowValue, Gwhere, null);
                    model.loveDataBase.update(addRowValue, Gwhere, null);
                }

                model.loveGroupAdapter.setChecked(-1);
                model.loveGroupAdapter.notifyDataSetChanged();
            } else {
                if (binding.loveList.getChoiceMode() == AbsListView.CHOICE_MODE_SINGLE) {
                    where = finish + '"' + model.customerGroups.get(finalChecked).getId() + '"';
                    where2 = "CustomerID = " + model.customerGroups.get(finalChecked).getCustomerId();
                    model.customerGroupDataBase.delete(where, null);
                    model.customerDataBase.delete(where2, null);
                    model.customerGroups.remove(finalChecked);
                    model.tempFiles.remove(finalChecked);
                } else {
                    for (int i = count - 1; i >= 0; i--) {
                        if (finalSp.get(i)) {
                            where = finish + '"' + model.customerGroups.get(i).getId() + '"';
                            where2 = "CustomerID = " + model.customerGroups.get(i).getCustomerId();
                            model.customerGroupDataBase.delete(where, null);
                            model.customerDataBase.delete(where2, null);
                            model.customerGroups.remove(i);
                            model.tempFiles.remove(i);
                        }
                    }
                }
                binding.loveList.clearChoices();

                for (int i = 0; i < model.customerGroupAdapter.getCount(); i++) {
                    model.customerGroups.get(i).setId(i + 1);
                    String Gwhere = "CustomerID = " + model.customerGroups.get(i).getCustomerId();
                    ContentValues addRowValue = new ContentValues();
                    addRowValue.put("CustomerID", i + 1);
                    model.customerGroupDataBase.update(addRowValue, Gwhere, null);
                    model.customerDataBase.update(addRowValue, Gwhere, null);
                }

                model.customerGroupAdapter.setChecked(-1);
                model.customerGroupAdapter.notifyDataSetChanged();
            }

            if (binding.loveList.getChoiceMode() == AbsListView.CHOICE_MODE_MULTIPLE) {
                binding.loveList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                if (model.loveKind.equals("myLove")) {
                    model.loveGroupAdapter.isChk(View.GONE);
                    model.loveGroupAdapter.notifyDataSetChanged();
                } else {
                    model.customerGroupAdapter.isChk(View.GONE);
                    model.customerGroupAdapter.notifyDataSetChanged();
                }
                binding.loveList.clearChoices();
                model.cl = 0;
            }
        });
    }

    @Override
    public void fourCall() {
        if (model.stat == 0) {
            binding.loveBot.setEnabled(true);
            binding.loveDown.setEnabled(true);
            binding.loveTop.setEnabled(true);
            binding.loveUp.setEnabled(true);
            binding.loveEdit.setBackgroundResource(R.drawable.click_sr_glsave);


            model.stat = 1;
        } else {
            binding.loveBot.setEnabled(false);
            binding.loveDown.setEnabled(false);
            binding.loveTop.setEnabled(false);
            binding.loveUp.setEnabled(false);
            binding.loveEdit.setBackgroundResource(R.drawable.click_sr_gledit);

            TimeSetFragment ta = TimeSetFragment.getInstance();
            Bundle bundle = new Bundle();
            bundle.putInt("kind", 5);
            ta.setArguments(bundle);
            ta.show(getChildFragmentManager(), "DIALOG");

            ta.setDialogR(finish -> {
                model.saveEdit(finish);

                if (binding.loveList.getChoiceMode() == AbsListView.CHOICE_MODE_MULTIPLE) {
                    binding.loveList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                    if (model.loveKind.equals("myLove")) {
                        model.loveGroupAdapter.isChk(View.GONE);
                        model.loveGroupAdapter.notifyDataSetChanged();
                    } else {
                        model.customerGroupAdapter.isChk(View.GONE);
                        model.customerGroupAdapter.notifyDataSetChanged();
                    }
                    binding.loveList.clearChoices();
                    model.cl = 0;
                }
            });
            model.stat = 0;
        }
    }

    @Override
    public void fiveCall() {
        int count, checked;
        if (model.loveKind.equals("myLove")) {
            count = model.loveGroupAdapter.getCount();
            if (count > 0) {
                checked = binding.loveList.getCheckedItemPosition();
                if ((checked - 1 > -1) && (checked < count)) {
                    LoveGroup checkList = model.loveGroups.get(checked);
                    LoveGroup chgList = model.loveGroups.get(checked - 1);

                    int x = checkList.getId();
                    checkList.setId(chgList.getId());
                    chgList.setId(x);

                    model.loveGroups.set(checked, chgList);
                    model.loveGroups.set(checked - 1, checkList);

                    binding.loveList.setItemChecked(checked - 1, true);
                    binding.loveList.smoothScrollToPosition(checked - 1);

                    model.loveGroupAdapter.setChecked(checked - 1);
                    model.loveGroupAdapter.notifyDataSetChanged();
                } else if (checked == 0) {
                    Toast.makeText(getContext(), "첫번째 목록입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "목록을 선택하거나 다중선택 모드를 해제하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            count = model.customerGroupAdapter.getCount();
            if (count > 0) {
                checked = binding.loveList.getCheckedItemPosition();
                if ((checked - 1 > -1) && (checked < count)) {
                    CustomerGroup checkList = model.customerGroups.get(checked);
                    CustomerGroup chgList = model.customerGroups.get(checked - 1);

                    int x = checkList.getId();
                    checkList.setId(chgList.getId());
                    chgList.setId(x);

                    model.customerGroups.set(checked, chgList);
                    model.customerGroups.set(checked - 1, checkList);
                    binding.loveList.setItemChecked(checked - 1, true);
                    binding.loveList.smoothScrollToPosition(checked - 1);

                    model.customerGroupAdapter.setChecked(checked - 1);
                    model.customerGroupAdapter.notifyDataSetChanged();
                } else if (checked == 0) {
                    Toast.makeText(getContext(), "첫번째 목록입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "목록을 선택하거나 다중선택 모드를 해제하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void sixCall() {
        int count, checked;
        if (model.loveKind.equals("myLove")) {
            count = model.loveGroupAdapter.getCount();
            if (count > 0) {
                checked = binding.loveList.getCheckedItemPosition();
                if ((checked > -1) && (checked + 1 < count)) {
                    LoveGroup checkList = model.loveGroups.get(checked);
                    LoveGroup chgList = model.loveGroups.get(checked + 1);

                    int x = checkList.getId();
                    checkList.setId(chgList.getId());
                    chgList.setId(x);

                    model.loveGroups.set(checked, chgList);
                    model.loveGroups.set(checked + 1, checkList);
                    binding.loveList.setItemChecked(checked + 1, true);
                    binding.loveList.smoothScrollToPosition(checked + 1);

                    model.loveGroupAdapter.setChecked(checked + 1);
                    model.loveGroupAdapter.notifyDataSetChanged();
                } else if (checked + 1 >= count) {
                    Toast.makeText(getContext(), "마지막 목록입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "목록을 선택하거나 다중선택 모드를 해제하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            count = model.customerGroupAdapter.getCount();
            if (count > 0) {
                checked = binding.loveList.getCheckedItemPosition();
                if ((checked > -1) && (checked + 1 < count)) {
                    CustomerGroup checkList = model.customerGroups.get(checked);
                    CustomerGroup chgList = model.customerGroups.get(checked + 1);

                    int x = checkList.getId();
                    checkList.setId(chgList.getId());
                    chgList.setId(x);

                    model.customerGroups.set(checked, chgList);
                    model.customerGroups.set(checked + 1, checkList);
                    binding.loveList.setItemChecked(checked + 1, true);
                    binding.loveList.smoothScrollToPosition(checked + 1);

                    model.customerGroupAdapter.setChecked(checked + 1);
                    model.customerGroupAdapter.notifyDataSetChanged();
                } else if (checked + 1 >= count) {
                    Toast.makeText(getContext(), "마지막 목록입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "목록을 선택하거나 다중선택 모드를 해제하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void sevenCall() {
        int count, checked, fchk;
        if (model.loveKind.equals("myLove")) {
            count = model.loveGroupAdapter.getCount();
            if (count > 0) {
                checked = binding.loveList.getCheckedItemPosition();
                fchk = binding.loveList.getHeaderViewsCount();
                if ((checked - 1 > -1) && (checked < count)) {
                    LoveGroup checkList = model.loveGroups.get(checked);
                    LoveGroup chgList = model.loveGroups.get(fchk);

                    int x = chgList.getId();

                    int pos = fchk + 1;
                    for (LoveGroup ignored : model.loveGroups) {
                        if (pos <= checked) {
                            LoveGroup list = model.loveGroups.get(pos);
                            chgList.setId(list.getId());
                            model.loveGroups.set(pos, chgList);
                            chgList = list;
                            pos++;
                        }
                    }

                    checkList.setId(x);

                    model.loveGroups.set(fchk, checkList);
                    binding.loveList.setItemChecked(fchk, true);
                    binding.loveList.smoothScrollToPosition(fchk);

                    model.loveGroupAdapter.setChecked(fchk);
                    model.loveGroupAdapter.notifyDataSetChanged();
                } else if (checked == 0) {
                    Toast.makeText(getContext(), "첫번째 목록입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "목록을 선택하거나 다중선택 모드를 해제하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            count = model.customerGroupAdapter.getCount();
            if (count > 0) {
                checked = binding.loveList.getCheckedItemPosition();
                fchk = binding.loveList.getHeaderViewsCount();
                if ((checked - 1 > -1) && (checked < count)) {
                    CustomerGroup checkList = model.customerGroups.get(checked);
                    CustomerGroup chgList = model.customerGroups.get(fchk);

                    int x = chgList.getId();

                    int pos = fchk + 1;
                    for (CustomerGroup ignored : model.customerGroups) {
                        if (pos <= checked) {
                            CustomerGroup list = model.customerGroups.get(pos);
                            chgList.setId(list.getId());
                            model.customerGroups.set(pos, chgList);
                            chgList = list;
                            pos++;
                        }
                    }

                    checkList.setId(x);

                    model.customerGroups.set(fchk, checkList);
                    binding.loveList.setItemChecked(fchk, true);
                    binding.loveList.smoothScrollToPosition(fchk);

                    model.customerGroupAdapter.setChecked(fchk);
                    model.customerGroupAdapter.notifyDataSetChanged();
                } else if (checked == 0) {
                    Toast.makeText(getContext(), "첫번째 목록입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "목록을 선택하거나 다중선택 모드를 해제하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void eightCall() {
        int count, checked, fchk;
        if (model.loveKind.equals("myLove")) {
            count = model.loveGroupAdapter.getCount();
            if (count > 0) {
                checked = binding.loveList.getCheckedItemPosition();
                fchk = count - 1;
                if ((checked > -1) && (checked + 1 < count)) {
                    LoveGroup checkList = model.loveGroups.get(checked);
                    LoveGroup chgList = model.loveGroups.get(fchk);

                    int x = chgList.getId();

                    int pos = fchk - 1;
                    for (LoveGroup ignored : model.loveGroups) {
                        if (checked <= pos) {
                            LoveGroup list = model.loveGroups.get(pos);
                            chgList.setId(list.getId());
                            model.loveGroups.set(pos, chgList);
                            chgList = list;
                            pos--;
                        }
                    }

                    checkList.setId(x);

                    model.loveGroups.set(fchk, checkList);
                    binding.loveList.setItemChecked(fchk, true);
                    binding.loveList.smoothScrollToPosition(fchk);

                    model.loveGroupAdapter.setChecked(fchk);
                    model.loveGroupAdapter.notifyDataSetChanged();
                } else if (checked + 1 >= count) {
                    Toast.makeText(getContext(), "마지막 목록입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "목록을 선택하거나 다중선택 모드를 해제하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            count = model.customerGroupAdapter.getCount();
            if (count > 0) {
                checked = binding.loveList.getCheckedItemPosition();
                fchk = count - 1;
                if ((checked > -1) && (checked + 1 < count)) {
                    CustomerGroup checkList = model.customerGroups.get(checked);
                    CustomerGroup chgList = model.customerGroups.get(fchk);

                    int x = chgList.getId();

                    int pos = fchk - 1;
                    for (CustomerGroup ignored : model.customerGroups) {
                        if (checked <= pos) {
                            CustomerGroup list = model.customerGroups.get(pos);
                            chgList.setId(list.getId());
                            model.customerGroups.set(pos, chgList);
                            chgList = list;
                            pos--;
                        }
                    }

                    checkList.setId(x);

                    model.customerGroups.set(fchk, checkList);
                    binding.loveList.setItemChecked(fchk, true);
                    binding.loveList.smoothScrollToPosition(fchk);

                    model.customerGroupAdapter.setChecked(fchk);
                    model.customerGroupAdapter.notifyDataSetChanged();
                } else if (checked + 1 >= count) {
                    Toast.makeText(getContext(), "마지막 목록입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "목록을 선택하거나 다중선택 모드를 해제하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void Idcol(getId get) {
        gt = get;
    }

    public interface getId {
        void Id(int id);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                if (binding.loveList.getChoiceMode() == AbsListView.CHOICE_MODE_MULTIPLE) {
                    binding.loveList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                    if (model.loveKind.equals("myLove")) {
                        model.loveGroupAdapter.isChk(View.GONE);
                        model.loveGroupAdapter.notifyDataSetChanged();
                    } else {
                        model.customerGroupAdapter.isChk(View.GONE);
                        model.customerGroupAdapter.notifyDataSetChanged();
                    }
                    binding.loveList.clearChoices();
                    model.cl = 0;
                } else {
                    dismiss();
                }
            }
        };
    }
}