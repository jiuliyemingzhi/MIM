package net.jiuli.mim.fragments.message;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.jiuli.common.app.Fragment;
import net.jiuli.common.app.PresenterFragment;
import net.jiuli.common.face.Face;
import net.jiuli.common.widget.DragBubbleView;
import net.jiuli.common.widget.PortraitView;
import net.jiuli.common.widget.impl.TextWatcherImpl;
import net.jiuli.common.widget.recycler.RecyclerAdapter;
import net.jiuli.factoylib.model.db.Message;
import net.jiuli.factoylib.model.db.User;
import net.jiuli.factoylib.persistence.Account;
import net.jiuli.factoylib.presenter.message.ChatContract;
import net.jiuli.mim.R;
import net.jiuli.mim.activities.MessageActivity;
import net.jiuli.mim.fragments.panel.PanelFragment;
import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.widget.Loading;
import net.qiujuer.widget.airpanel.AirPanel;
import net.qiujuer.widget.airpanel.Util;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */

public abstract class ChatFragment<InitModel>
        extends PresenterFragment<ChatContract.Presenter>
        implements ChatContract.View<InitModel>,
        View.OnClickListener, PanelFragment.PanelCallback {

    protected String mReceiverId;

    protected Adapter mAdapter;

    protected int MIN_DY;


    private boolean keyBoardIsShow = false;

    @BindView(R.id.recycler_message)
    RecyclerView mRecyclerView;

    @BindView(R.id.recycler_member)
    RecyclerView mRecyclerMember;


    @BindView(R.id.appbar)
    AppBarLayout mAppbar;

    @BindView(R.id.edit_content)
    EditText etContent;

    @BindView(R.id.btn_face)
    ImageView ivFace;

    @BindView(R.id.btn_record)
    ImageView ivRecord;

    @BindView(R.id.btn_submit)
    ImageView ivSubmit;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.iv_menu)
    ImageView ivMenu;


    @BindView(R.id.tv_sub_title)
    TextView tvSubTitle;

    @BindView(R.id.drag_bubble)
    DragBubbleView dragBubble;


    protected AirPanel.Boss mPanelBoss;

    private PanelFragment mPanelFragment;


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_chat_commom;
    }


    @Override
    protected void initArgs(Bundle bundle) {
        super.initArgs(bundle);
        mReceiverId = bundle.getString(MessageActivity.KEY_RECEIVER_ID);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.start();
        MIN_DY = (int) Ui.dipToPx(getResources(), 90);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        mPanelBoss = root.findViewById(R.id.message_root);
        mPanelBoss.setup(new AirPanel.PanelListener() {
            @Override
            public void requestHideSoftKeyboard() {
                Util.hideKeyboard(etContent);
            }
        });

        mPanelFragment = (PanelFragment) getChildFragmentManager().findFragmentById(R.id.frag_panel);
        mPanelFragment.setCallback(this);
        mPanelFragment.setDragBubble(dragBubble);
        initEditContent();

        ivFace.setOnClickListener(this);
        ivSubmit.setOnClickListener(this);
        ivRecord.setOnClickListener(this);
        root.findViewById(R.id.frame_back).setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter = new Adapter());


        mRecyclerView.setOnTouchListener(new OnTouchListenerImpl());

        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            private float minDelta = Ui.dipToPx(getResources(), 60);

            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (oldBottom == bottom || Math.abs(oldBottom - bottom) < minDelta) {
                    return;
                }
                keyBoardIsShow = oldBottom > bottom;
                if (!getRecyclerIsShow()) {
                    return;
                }
                etContent.post(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerMember.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    class OnTouchListenerImpl implements View.OnTouchListener {
        private VelocityTracker mVelocityTracker = VelocityTracker.obtain();
        private int py = 0;
        private int dy = 0;

        @Override
        public boolean onTouch(View v, MotionEvent event) {


            mVelocityTracker.addMovement(event);

            switch (event.getAction()) {

                case MotionEvent.ACTION_MOVE:

                    if (py == 0) {
                        py = (int) event.getRawY();
                        break;
                    }

                    dy = (int) (event.getRawY() - py);

                    if (getRecyclerIsShow()) {
                        mRecyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                mRecyclerMember.setVisibility(View.GONE);
                            }
                        });
                    }
                    if (keyBoardIsShow) {

                        Util.hideKeyboard(etContent);
                        if (mPanelBoss.isOpen()) {
                            mPanelBoss.closePanel();
                        }

                    }

                    break;

                case MotionEvent.ACTION_UP: {
                    if (dy < -MIN_DY && !mRecyclerView.canScrollVertically(1)) {

                        mVelocityTracker.computeCurrentVelocity(40);

                        float yVelocity = mVelocityTracker.getYVelocity(0);

                        if (Math.abs(yVelocity) >= MIN_DY) {
                            Util.showKeyboard(etContent);
                        }
                    }

                    py = 0;
                }
                break;
            }
            return false;
        }
    }

    private static String before_text;

    public static void setBefore_text(String beforeText) {
        ChatFragment.before_text = beforeText;
    }

    protected boolean getRecyclerIsShow() {
        return mRecyclerMember.getVisibility() == View.VISIBLE;
    }

    private void initEditContent() {
        ivSubmit.setActivated(false);

        etContent.addTextChangedListener(new TextWatcherImpl() {

            @Override
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                int length = c.length();
                ivSubmit.setActivated(length > 0);
                if (before_text != null && before_text.equals(c.toString())) {
                    return;
                }
                before_text = c.toString();
                start = start - 6;
                int end = etContent.getSelectionEnd() + 6;
                CharSequence input = c.subSequence(start = start < 0 ? 0 : start, end > length ? length : end);
                Pattern pattern = Pattern.compile("(\\[f[tb]\\d{3}])");
                final Matcher matcher = pattern.matcher(input);

                int size = (int) (etContent.getTextSize() * 1.2);

                while (matcher.find()) {
                    String key = matcher.group().replaceAll("[\\[\\]]", "");
                    Face.Bean bean = Face.beans().get(key);
                    if (bean != null) {
                        Face.inputFace(getContext(), etContent.getText(), bean, size, start + matcher.start(), start + matcher.start() + 7);
                    }
                }
            }
        });
    }


    @Override
    public EditText getInputEditText() {
        return etContent;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                if (ivSubmit.isActivated()) {
                    String content = etContent.getText().toString();
                    etContent.setText("");
                    mPresenter.pushText(content);
                } else {
                    onMoreClick();
                }
                break;
            case R.id.btn_face:
                mPanelBoss.openPanel();
                mPanelFragment.showFace();
                break;
            case R.id.btn_record:
                mPanelBoss.openPanel();
                mPanelFragment.showRecord();
                break;
            case R.id.frame_back:
                getActivity().finish();
                break;
            default:

                break;
        }
    }


    private void onMoreClick() {
        mPanelBoss.openPanel();
        mPanelFragment.showGallery();
    }

    private class Adapter extends RecyclerAdapter<Message> {

        @Override
        public int getItemViewType(int position, Message message) {
            boolean isRight = Objects.equals(message.getSender().getId(), Account.getUserId());
            switch (message.getType()) {
                case Message.TYPE_PIC:
                    return isRight ? R.layout.cell_chat_pic_right : R.layout.cell_chat_pic_left;
                case Message.TYPE_AUDIO:
                    return isRight ? R.layout.cell_chat_audio_right : R.layout.cell_chat_audio_left;
                case Message.TYPE_VIDEO:
                    return isRight ? R.layout.cell_chat_video_right : R.layout.cell_chat_video_left;
                case Message.TYPE_FILE:
                    return isRight ? R.layout.cell_chat_file_right : R.layout.cell_chat_file_left;
                case Message.TYPE_STR:
                default:
                    return isRight ? R.layout.cell_chat_text_right : R.layout.cell_chat_text_left;
            }
        }

        @Override
        protected ViewHolder<Message> onCreateViewHolder(View root, int viewType) {
            switch (viewType) {
                case R.layout.cell_chat_text_right:
                case R.layout.cell_chat_text_left:
                    return new TextHolder(root);
                case R.layout.cell_chat_pic_right:
                case R.layout.cell_chat_pic_left:
                    return new PicHolder(root);
                case R.layout.cell_chat_audio_right:
                case R.layout.cell_chat_audio_left:
                    return new AudioHolder(root);
                case R.layout.cell_chat_video_right:
                case R.layout.cell_chat_video_left:
                    return new VideoHolder(root);
                case R.layout.cell_chat_file_right:
                case R.layout.cell_chat_file_left:
                    return new FileHolder(root);
                default:
                    return new TextHolder(root);

            }
        }
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<Message> {
        @BindView(R.id.portrait)
        PortraitView mPortraitView;

        @Nullable
        @BindView(R.id.loading)
        Loading mLoading;

        ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            User sender = message.getSender();
            sender.load();
            mPortraitView.setup(Glide.with(ChatFragment.this), sender.getPortrait());
            if (mLoading != null) {

                switch (message.getStatus()) {
                    case Message.STATUS_DONE:
                        mLoading.stop();
                        mLoading.setVisibility(View.GONE);
                        break;
                    case Message.STATUS_CREATED:
                        mLoading.setVisibility(View.VISIBLE);
                        mLoading.setProgress(0f);
                        mLoading.setForegroundColor(UiCompat.getColor(getResources(), R.color.colorAccent));
                        mLoading.start();
                        break;
                    case Message.STATUS_FAILED:
                        mLoading.setVisibility(View.VISIBLE);
                        mLoading.stop();
                        mLoading.setProgress(1f);
                        mLoading.setForegroundColor(UiCompat.getColor(getResources(), R.color.alertImportant));
                        break;
                }
                mPortraitView.setEnabled(message.getStatus() == Message.STATUS_FAILED);
            }
        }

        @OnClick(R.id.portrait)
        void onRePushClick() {
            if (mLoading != null && mPresenter.rePush(mData)) {
                updateData(mData);
            }
        }
    }

    public class TextHolder extends ViewHolder {

        @BindView(R.id.tv_content)
        TextView tvContent;


        TextHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            Face.decode(tvContent, message.getContent(), tvContent.getTextSize() * 2.1f, Face.TYPE_CHAT);
        }
    }

    class AudioHolder extends ViewHolder {

        AudioHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            //TODO
        }
    }

    class PicHolder extends ViewHolder {

        PicHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            //TODO
        }
    }

    class FileHolder extends ViewHolder {


        FileHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            //TODO
        }
    }

    class VideoHolder extends ViewHolder {

        VideoHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            //TODO
        }
    }

    @Override
    public void onAdapterDataChange() {
        if (mAdapter.getItemCount() > 0) {
            mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
        }
    }


    @Override
    public RecyclerAdapter<Message> getRecyclerViewAdapter() {
        return mAdapter;
    }

}
