package com.sunsunsoft.shutaro.tangobook.icon;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;

import com.sunsunsoft.shutaro.tangobook.util.ULog;
import com.sunsunsoft.shutaro.tangobook.util.UColor;

import java.util.LinkedList;
import java.util.List;

/**
 * アイコンを内包するRectを管理するクラス
 * 配下のアイコンが全て収まる大きなRectを求めておき、
 * まずはこの大きなRectと判定を行い、重なっていた場合にのみ個々のアイコンと判定する
 */
public class UIconsBlockManager {
    public static final String TAG = "UIconsBlockManager";
    LinkedList<IconsBlock> blockList = new LinkedList<>();
    List<UIcon> icons;

    /**
     * インスタンスを取得
     * @param icons
     * @return
     */
    public static UIconsBlockManager createInstance(List<UIcon> icons) {
        UIconsBlockManager instance = new UIconsBlockManager();
        instance.icons = icons;
        return instance;
    }

    /**
     * アイコンリストを設定する
     * アイコンリストはアニメーションが終わって座標が確定した時点で行う
     */
    public void setIcons(List<UIcon> icons) {
        this.icons = icons;
        update();
    }

    /**
     * IconsBlockのリストを作成する
     */
    public void update() {
        if (icons == null) return;
        if (blockList != null) {
            blockList.clear();
        }

        IconsBlock block = null;
        for (UIcon icon : icons) {
            if (block == null) {
                block = new IconsBlock();
            }

            if (block.add(icon)) {
                // ブロックがいっぱいになったのでRectを更新してから次のブロックを作成する
                block.updateRect();
                blockList.add(block);
                // 次のアイコンがあるとも限らないのでここでからにしておく
                block = null;
            }
        }

        if (block != null) {
            block.updateRect();
            blockList.add(block);
        }
    }

    /**
     * 指定座標に重なるアイコンを取得する
     * @param pos
     * @return
     */
    public UIcon getOverlapedIcon(Point pos, List<UIcon> exceptIcons) {
        //ULog.startCount(TAG);
        for (IconsBlock block : blockList) {
            UIcon icon = block.getOverlapedIcon(pos, exceptIcons);
            if (icon != null) {
                //ULog.endCount(TAG);
                return icon;
            }
        }
        //ULog.endCount(TAG);
        return null;
    }

    private void showLog() {
        // debug
        for (IconsBlock block : blockList) {
            Rect _rect = block.getRect();
            ULog.print(TAG, "count:" + block.getIcons().size() + " L:" + _rect.left + " R:" + _rect
                    .right +
                    " " +
                    "U:" +
                    _rect.top + " D:" + _rect.bottom);
        }
    }

    /**
     * IconsBlockの矩形を描画 for Debug
     * @param canvas
     * @param paint
     */
    public void draw(Canvas canvas, Paint paint, PointF toScreenPos) {
        for (IconsBlock block : blockList) {
            block.draw(canvas, paint, toScreenPos);
        }
    }
}

/**
 * １ブロックのクラス
 */
class IconsBlock {
    private static final int BLOCK_ICON_MAX = 8;

    private LinkedList<UIcon> icons = new LinkedList<>();
    private Rect rect = new Rect();
    private int color = UColor.BLACK;

    // Get/Set
    public Rect getRect() {
        return rect;
    }

    public LinkedList<UIcon> getIcons() {
        return icons;
    }

    /**
     * アイコンをブロックに追加する
     * @param icon
     * @return true:リストがいっぱい
     */
    public boolean add(UIcon icon) {
        icons.add(icon);
        if (icons.size() >= BLOCK_ICON_MAX) {
            return true;
        }
        return false;
    }

    /**
     * ブロックの矩形を更新
     */
    public void updateRect() {
        rect.left = 1000000;
        rect.top = 1000000;
        for (UIcon icon : icons) {
            if (icon.getPosX() < rect.left) {
                rect.left = (int)icon.getPosX();
            }
            if (icon.getRight() > rect.right) {
                rect.right = (int)icon.getRight();
            }
            if (icon.getPosY() < rect.top) {
                rect.top = (int)icon.getPosY();
            }
            if (icon.getBottom() > rect.bottom) {
                rect.bottom = (int)icon.getBottom();
            }
        }
    }

    /**
     * ブロックとの重なり判定
     * ブロックと重なっていたら個々のアイコンとも判定を行う
     * @param pos
     * @param exceptIcons
     * @return null:重なるアイコンなし
     */
    public UIcon getOverlapedIcon(Point pos, List<UIcon> exceptIcons) {

        if (rect.contains(pos.x, pos.y)) {
            for (UIcon icon : icons) {
                if (exceptIcons.contains(icon)) continue;

                ULog.count(UIconsBlockManager.TAG);
                if (icon.getRect().contains(pos.x, pos.y)) {
                    return icon;
                }
            }
        }
        return null;
    }

    /**
     * 矩形を描画(for Debug)
     * @param canvas
     * @param paint
     */
    public void draw(Canvas canvas, Paint paint, PointF toScreenPos) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(color);
        canvas.drawRect(rect.left + toScreenPos.x, rect.top + toScreenPos.y,
                rect.right + toScreenPos.x, rect.bottom + toScreenPos.y,  paint);
    }
}
