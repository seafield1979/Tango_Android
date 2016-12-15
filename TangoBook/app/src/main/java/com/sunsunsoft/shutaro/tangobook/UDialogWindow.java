package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.LinkedList;

/**
 * ダイアログ(画面の最前面に表示されるWindow)
 *
 * 使用方法
 *  UDialogWindow dialog = UDialogWindow(...);
 *  dialog.addButton(...)       ボタン追加
 *  dialog.addButton(...)       ボタン追加
 *      ...                     好きなだけボタン追加
 *  dialog.updateLayout(...)    レイアウト確定
 *
 */
interface UDialogCallbacks {
    void dialogClosed(UDialogWindow dialog);
}

public class UDialogWindow extends UWindow implements UButtonCallbacks{

    enum DialogType {
        Normal,     // 移動可能、下にあるWindowをタッチできる
        Mordal      // 移動不可、下にあるWindowをタッチできない
    }

    enum DialogPosType {
        Point,      // 指定した座標に表示
        Center      // 中央に表示
    }

    // ボタンの並ぶ方向
    enum ButtonDir {
        Horizontal,     // 横に並ぶ
        Vertical        // 縦に並ぶ
    }

    enum AnimationType {
        Opening,        // ダイアログが開くときのアニメーション
        Closing         // ダイアログが閉じる時のアニメーション
    }

    public static final int CloseDialogId = 10000123;

    protected static final int MARGIN_H = 100;
    protected static final int ANIMATION_FRAME = 10;

    protected static final int MESSAGE_TEXT_SIZE = 50;
    protected static final int TEXT_MARGIN_V = 50;
    protected static final int BUTTON_H = 140;
    protected static final int BUTTON_MARGIN_H = 50;
    protected static final int BUTTON_MARGIN_V = 30;
    protected static final int TITLE_TEXT_SIZE = 50;

    /**
     * Member variables
     */
    protected PointF basePos;       // Open/Close時の中心座標
    protected DialogType type;
    protected DialogPosType posType;
    protected ButtonDir buttonDir;

    protected int textColor;
    protected int dialogColor;
    protected int bgColor;

    protected UButtonCallbacks buttonCallbacks;
    protected UDialogCallbacks dialogCallbacks;
    protected AnimationType animationType;
    protected boolean isAnimation;
    protected Paint paint;

    protected Size screenSize = new Size();

    protected boolean isUpdate = true;     // ボタンを追加するなどしてレイアウトが変更された

    // タイトル
    protected String title;
    protected UTextView mTitleView;

    // メッセージ(複数)
    protected LinkedList<UTextView> mTextViews = new LinkedList<>();

    // ボタン(複数)
    protected LinkedList<UButton> mButtons = new LinkedList<>();

    /**
     * Get/Set
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private void updateBasePos() {
        if (posType == DialogPosType.Point) {
            basePos = new PointF(pos.x + size.width / 2, pos.y + size.height / 2);
        } else {
            basePos = new PointF(screenSize.width / 2, screenSize.height / 2);
        }
    }

    /**
     * Constructor
     */
    public UDialogWindow(DialogType type, UButtonCallbacks buttonCallbacks,
                         UDialogCallbacks dialogCallbacks,
                         ButtonDir dir, DialogPosType posType,
                         boolean isAnimation,
                         float x, float y,
                         int screenW, int screenH,
                         int textColor, int dialogColor)
    {
        super(null, DrawPriority.Dialog.p(), x, y, screenW, screenH, dialogColor);
        size = new Size(screenW - MARGIN_H * 2, screenH - MARGIN_H * 2);
        this.type = type;
        this.posType = posType;
        this.buttonDir = dir;
        this.paint = new Paint();
        this.textColor = textColor;
        this.dialogColor = dialogColor;
        this.buttonCallbacks = buttonCallbacks;
        this.dialogCallbacks = dialogCallbacks;
        this.isAnimation = isAnimation;

        screenSize.width = screenW;
        screenSize.height = screenH;

    }

    // 座標指定タイプ
    public static UDialogWindow createInstance(DialogType type, UButtonCallbacks buttonCallbacks,
                                               UDialogCallbacks dialogCallbacks,
                                               ButtonDir dir, DialogPosType posType,
                                               boolean isAnimation,
                                               float x, float y,
                                               int screenW, int screenH,
                                               int textColor, int dialogColor)
    {
        UDialogWindow instance = createInstance(type,
                buttonCallbacks, dialogCallbacks,
                dir, posType, isAnimation,
                screenW, screenH, textColor, dialogColor);
        instance.posType = DialogPosType.Point;
        instance.pos.x = x;
        instance.pos.y = y;

        return instance;
    }

    // 画面中央に表示するタイプ
    public static UDialogWindow createInstance(DialogType type, UButtonCallbacks buttonCallbacks,
                                               UDialogCallbacks dialogCallbacks,
                                               ButtonDir dir, DialogPosType posType,
                                               boolean isAnimation,
                                               int screenW, int screenH,
                                               int textColor, int dialogColor)
    {
        UDialogWindow instance = new UDialogWindow( type, buttonCallbacks,
                dialogCallbacks, dir, posType, isAnimation, 0, 0, screenW, screenH,
                textColor, dialogColor);
        // ダミーのサイズ

        instance.type = type;
        instance.posType = DialogPosType.Center;
        instance.buttonDir = dir;
        instance.paint = new Paint();
        instance.textColor = textColor;
        instance.dialogColor = dialogColor;
        instance.buttonCallbacks = buttonCallbacks;
        instance.dialogCallbacks = dialogCallbacks;
        instance.isAnimation = isAnimation;
        if (isAnimation) {
            instance.updateBasePos();
            instance.startAnimation(AnimationType.Opening);
        }
        if (type == DialogType.Mordal) {
            instance.bgColor = Color.argb(160,0,0,0);
        }

        // 描画はDrawManagerに任せるのでDrawManagerに登録
        UDrawManager.getInstance().addDrawable(instance);

        return instance;
    }

    // 最小限の引数で作成
    public static UDialogWindow createInstance(UButtonCallbacks buttonCallbacks,
                                               int screenW, int screenH)
    {
        return createInstance(DialogType.Mordal, buttonCallbacks,
                null,
                ButtonDir.Horizontal, DialogPosType.Center,
                true, screenW, screenH,
                Color.BLACK, Color.WHITE);
    }

    public void setDialogPos(float x, float y) {
        pos.x = x;
        pos.y = y;
    }

    public void setDialogPosCenter() {
        pos.x = (size.width - size.width) / 2;
        pos.y = (size.height - size.height) / 2;
    }

    public boolean doAction() {
        return false;
    }

    /**
     * ボタンを全削除
     */
    public void clearButtons() {
        mButtons.clear();
    }

    /**
     * ダイアログを閉じる
     */
    public void closeDialog() {
        isShow = false;
        UDrawManager.getInstance().removeDrawable(this);
        if (dialogCallbacks != null) {
            dialogCallbacks.dialogClosed(this);
        }
    }

    /**
     * 閉じるアニメーション開始
     */
    public void startClosing() {
        updateBasePos();

        if (isAnimation) {
            startAnimation(AnimationType.Closing);
        } else {
            closeDialog();
        }
    }

    /**
     * TextViewを追加
     */
    public UTextView addTextView(String text, UAlignment alignment,
                                 boolean multiLine, boolean isDrawBG,
                                 int textSize, int textColor,
                                 int bgColor)
    {
        float x = 0;
        switch(alignment) {
            case CenterX:
            case Center:
                x = size.width / 2;
                break;
            case CenterY:
                x = MARGIN_H;
                break;
        }
        UTextView textView = UTextView.createInstance(text, textSize, 0,
                alignment, screenSize.width,
                multiLine, isDrawBG, x, 0, size.width - MARGIN_H * 2, textColor, bgColor);
        mTextViews.add(textView);
        isUpdate = true;
        return textView;
    }

    /**
     * ボタンを追加
     * ボタンを追加した時点では座標は設定しない
     * @param text
     * @param color
     */
    public UButton addButton(int id, String text, int textColor, int color) {
        UButtonText button = new UButtonText(buttonCallbacks, UButtonType.Press,
                id, 0, text, 0, 0,
                0, 0,
                50, textColor, color);
        mButtons.add(button);
        isUpdate = true;
        return button;
    }

    /**
     * ダイアログを閉じるボタンを追加する
     * @param text
     */
    public void addCloseButton(String text) {
        if (text == null) {
            text = "Close";
        }
        UButtonText button = new UButtonText(this, UButtonType.Press, CloseDialogId,
                0, text, 0, 0,
                0, 0,
                50, Color.WHITE, Color.rgb(200,100,100));
        mButtons.add(button);
        isUpdate = true;
    }

    /**
     * アイコンボタンを追加
     */
    public void addImageButton(int id, int imageId, int pressedImageId, int width, int height) {
        if (imageId == -1) {
            return;
        }

        UButtonImage button = UButtonImage.createButton(buttonCallbacks, id,
                0,
                0, 0, width, height,
                imageId, pressedImageId);
        mButtons.add(button);
        isUpdate = true;
    }

    /**
     * レイアウトを更新
     * ボタンの数によってレイアウトは自動で変わる
     */
    protected void updateLayout(Canvas canvas) {
        // タイトル、メッセージ
        int y = TEXT_MARGIN_V;
        if (title != null && mTitleView == null) {
            mTitleView = UTextView.createInstance(title, TITLE_TEXT_SIZE, 0, UAlignment.CenterX,
                    canvas.getWidth(), true, true,
                    size.width / 2, y,
                    size.width, color, 0);
            Size titleSize = mTitleView.getTextSize(getWidth());
            y += titleSize.height + BUTTON_MARGIN_H;
        }

        // テキスト
        for (UTextView textView : mTextViews) {
            textView.pos.y = y;
            y += textView.size.height + BUTTON_MARGIN_H;
        }

        // ボタン
        if (buttonDir == ButtonDir.Horizontal) {
            // ボタンを横に並べる
            // 画像ボタンのサイズはそのままにする
            // 固定サイズの画像ボタンと可変サイズのボタンが混ざっていても正しく配置させるためにいろいろ計算
            int num = mButtons.size();
            int imageNum = 0;
            int imagesWidth = 0;
            for (UButton button : mButtons) {
                if (button instanceof UButtonImage) {
                    imageNum++;
                    imagesWidth += button.size.width;
                }
            }

            int buttonW = (size.width - (((num + 1) * BUTTON_MARGIN_H) + imagesWidth)) / (num - imageNum);
            float x = BUTTON_MARGIN_H;
            int heightMax = 0;
            int _height;
            for (int i=0; i<num; i++) {
                UButton button = mButtons.get(i);
                if (button instanceof UButtonImage) {
                    button.setPos(x, y);
                    x += button.size.width + BUTTON_MARGIN_H;
                    _height = button.size.height;
                } else {
                    button.setPos(x, y);
                    button.setSize(buttonW, BUTTON_H);
                    x += buttonW + BUTTON_MARGIN_H;
                    _height = BUTTON_H;
                }
                if (_height > heightMax) {
                    heightMax = _height;
                }
            }
            y += heightMax + BUTTON_MARGIN_H;
        }
        else {
            // ボタンを縦に並べる
            int num = mButtons.size();

            for (int i=0; i<num; i++) {
                UButton button = mButtons.get(i);
                if (button instanceof UButtonImage) {
                    button.setPos((size.width - button.size.width) / 2, y);
                    y += button.size.height + BUTTON_MARGIN_V;
                } else {
                    button.setPos(BUTTON_MARGIN_H, y);
                    button.setSize(size.width - BUTTON_MARGIN_H * 2, BUTTON_H);
                    y += BUTTON_H + BUTTON_MARGIN_V;
                }
            }
        }
        size.height = y;

        // センタリング
        pos.x = (screenSize.width - size.width) / 2;
        pos.y = (screenSize.height - size.height) / 2;
        updateRect();
    }

    /**
     * 描画処理
     * @param canvas
     * @param paint
     * @param offset 独自の座標系を持つオブジェクトをスクリーン座標系に変換するためのオフセット値
     */
    public void draw(Canvas canvas, Paint paint, PointF offset) {
        if (!isShow) return;
        if (isUpdate) {
            isUpdate = false;
            updateLayout(canvas);
        }

        // BG
        if (type == DialogType.Mordal) {
            UDraw.drawRectFill(canvas, paint,
                    new Rect(0, 0, screenSize.width, screenSize.height), bgColor, 0, 0);
        }

        super.draw(canvas, paint, offset);
    }

    /**
     * コンテンツを描画する
     * @param canvas
     * @param paint
     */
    public void drawContent(Canvas canvas, Paint paint, PointF offset) {

        if (isAnimating) {
            // Open/Close animatin
            float ratio;
            if (animationType == AnimationType.Opening) {
                ratio = (float)Math.sin(animeRatio * 90 * RAD);
                ULog.print(TAG, "ratio:" + ratio);
            } else {
                ratio = (float)Math.cos(animeRatio * 90 * RAD);
                ULog.print(TAG, "cos ratio:" + ratio);
            }

            float width, height, x, y;
            RectF _rect;

            width = size.width * ratio;
            height = size.height * ratio;
            x = basePos.x - width / 2;
            y = basePos.y - height / 2;
            _rect = new RectF(x, y, x + width, y + height);

            drawBG(canvas, paint, _rect);
        } else {
            // BG
            drawBG(canvas, paint, rect);

            // Title
            PointF _offset = pos;
            if (mTitleView != null) {
                mTitleView.draw(canvas, paint, _offset);
            }
            // TextViews
            for (UTextView textView : mTextViews) {
                textView.draw(canvas, paint, _offset);
            }

            // Buttons
            for (UButton button : mButtons) {
                button.draw(canvas, paint, _offset);
            }
        }
    }

    public RectF getDialogRect() {
        return new RectF(pos.x, pos.y, pos.x + size.width, pos.y + size.height);
    }

    /**
     * タッチ処理
     * @param vt
     * @return
     */
    public boolean touchEvent(ViewTouch vt) {
        PointF offset = pos;

        boolean isRedraw = false;

        // タッチアップ処理
        for (UButton button : mButtons) {
            if (button.touchUpEvent(vt)) {
                isRedraw = true;
            }
        }
        // タッチ処理
        for (UButton button : mButtons) {
            if (button.touchEvent(vt, offset)) {
                return true;
            }
        }

        // 範囲外をタッチしたら閉じる
        if (type == DialogType.Normal) {
            if (vt.type == TouchType.Touch) {
                if (!getDialogRect().contains(vt.touchX(), vt.touchY())) {
                    startClosing();
                    return true;
                }
            }
        }
        // モーダルなら他のオブジェクトにタッチ処理を渡さない
        if (type == DialogType.Mordal) {
            if (vt.type == TouchType.Touch ||
                    vt.type == TouchType.LongPress ||
                    vt.type == TouchType.Click ||
                    vt.type == TouchType.LongClick )
            {
                return true;
            }
        }

        if (super.touchEvent(vt)) {
            return true;
        }

        return isRedraw;
    }

    public void startAnimation(AnimationType type) {
        animationType = type;
        startAnimation(ANIMATION_FRAME);
    }

    public void endAnimation() {
        if (animationType == AnimationType.Closing) {
            closeDialog();
        }
    }

    /**
     * UButtonCallbacks
     */
    public boolean UButtonClicked(int id, boolean pressedOn) {
        switch(id) {
            case CloseButtonId:
            case CloseDialogId:
                if (isAnimation) {
                    startClosing();
                } else {
                    closeDialog();
                }
                return true;
        }
        if (super.UButtonClicked(id, pressedOn)) {
            return true;
        }
        return false;
    }
}
