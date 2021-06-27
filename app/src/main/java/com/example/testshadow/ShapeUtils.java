package com.example.testshadow;


import android.graphics.Path;
import android.graphics.Path.Direction;

final class ShapeUtils {
    ShapeUtils() {
    }

    static Path roundedRect(float left, float top, float right, float bottom, float rx, float ry, boolean tl, boolean tr, boolean br, boolean bl) {
        Path path = new Path();
        if (rx < 0.0F) {
            rx = 0.0F;
        }

        if (ry < 0.0F) {
            ry = 0.0F;
        }

        float width = right - left;
        float height = bottom - top;
        if (rx > width / 2.0F) {
            rx = width / 2.0F;
        }

        if (ry > height / 2.0F) {
            ry = height / 2.0F;
        }

        float widthMinusCorners = width - 2.0F * rx;
        float heightMinusCorners = height - 2.0F * ry;
        path.moveTo(right, top + ry);
        if (tr) {
            path.rQuadTo(0.0F, -ry, -rx, -ry);
        } else {
            path.rLineTo(0.0F, -ry);
            path.rLineTo(-rx, 0.0F);
        }

        path.rLineTo(-widthMinusCorners, 0.0F);
        if (tl) {
            path.rQuadTo(-rx, 0.0F, -rx, ry);
        } else {
            path.rLineTo(-rx, 0.0F);
            path.rLineTo(0.0F, ry);
        }

        path.rLineTo(0.0F, heightMinusCorners);
        if (bl) {
            path.rQuadTo(0.0F, ry, rx, ry);
        } else {
            path.rLineTo(0.0F, ry);
            path.rLineTo(rx, 0.0F);
        }

        path.rLineTo(widthMinusCorners, 0.0F);
        if (br) {
            path.rQuadTo(rx, 0.0F, rx, -ry);
        } else {
            path.rLineTo(rx, 0.0F);
            path.rLineTo(0.0F, -ry);
        }

        path.rLineTo(0.0F, -heightMinusCorners);
        path.close();
        return path;
    }

    static Path roundedRect(float left, float top, float right, float bottom, float tl, float tr, float br, float bl) {
        Path path = new Path();
        if (tl < 0.0F) {
            tl = 0.0F;
        }

        if (tr < 0.0F) {
            tr = 0.0F;
        }

        if (br < 0.0F) {
            br = 0.0F;
        }

        if (bl < 0.0F) {
            bl = 0.0F;
        }

        float width = right - left;
        float height = bottom - top;
        float min = Math.min(width, height);
        if (tl > min / 2.0F) {
            tl = min / 2.0F;
        }

        if (tr > min / 2.0F) {
            tr = min / 2.0F;
        }

        if (br > min / 2.0F) {
            br = min / 2.0F;
        }

        if (bl > min / 2.0F) {
            bl = min / 2.0F;
        }

        if (tl == tr && tr == br && br == bl && tl == min / 2.0F) {
            float radius = min / 2.0F;
            path.addCircle(left + radius, top + radius, radius, Direction.CW);
            return path;
        } else {
            path.moveTo(right, top + tr);
            if (tr > 0.0F) {
                path.rQuadTo(0.0F, -tr, -tr, -tr);
            } else {
                path.rLineTo(0.0F, -tr);
                path.rLineTo(-tr, 0.0F);
            }

            path.rLineTo(-(width - tr - tl), 0.0F);
            if (tl > 0.0F) {
                path.rQuadTo(-tl, 0.0F, -tl, tl);
            } else {
                path.rLineTo(-tl, 0.0F);
                path.rLineTo(0.0F, tl);
            }

            path.rLineTo(0.0F, height - tl - bl);
            if (bl > 0.0F) {
                path.rQuadTo(0.0F, bl, bl, bl);
            } else {
                path.rLineTo(0.0F, bl);
                path.rLineTo(bl, 0.0F);
            }

            path.rLineTo(width - bl - br, 0.0F);
            if (br > 0.0F) {
                path.rQuadTo(br, 0.0F, br, -br);
            } else {
                path.rLineTo(br, 0.0F);
                path.rLineTo(0.0F, -br);
            }

            path.rLineTo(0.0F, -(height - br - tr));
            path.close();
            return path;
        }
    }
}