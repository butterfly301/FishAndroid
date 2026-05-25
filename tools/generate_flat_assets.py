from pathlib import Path
from PIL import Image, ImageChops, ImageDraw, ImageFilter, ImageFont, ImageOps


ASSET_DIR = Path(r"E:\FishAndroid\app\src\main\assets")

PALETTE = {
    "sea_top": (23, 144, 199),
    "sea_mid": (18, 111, 169),
    "sea_bottom": (11, 62, 110),
    "teal": (50, 196, 202),
    "mint": (121, 232, 214),
    "yellow": (255, 204, 84),
    "orange": (255, 132, 90),
    "pink": (255, 108, 136),
    "violet": (130, 115, 232),
    "indigo": (83, 96, 220),
    "slate": (52, 74, 108),
    "foam": (232, 247, 255),
    "white": (255, 255, 255),
    "shadow": (9, 29, 59),
    "leaf_1": (46, 190, 156),
    "leaf_2": (31, 154, 126),
}


def load_font(size, bold=False):
    candidates = []
    if bold:
        candidates += [
            r"C:\Windows\Fonts\segoeuib.ttf",
            r"C:\Windows\Fonts\arialbd.ttf",
            r"C:\Windows\Fonts\msyhbd.ttc",
        ]
    candidates += [
        r"C:\Windows\Fonts\segoeui.ttf",
        r"C:\Windows\Fonts\arial.ttf",
        r"C:\Windows\Fonts\msyh.ttc",
    ]
    for path in candidates:
        if Path(path).exists():
            return ImageFont.truetype(path, size)
    return ImageFont.load_default()


FONT_XL = load_font(34, bold=True)
FONT_L = load_font(20, bold=True)
FONT_M = load_font(14, bold=True)
FONT_S = load_font(10, bold=True)


def vertical_gradient(size, top, bottom):
    width, height = size
    img = Image.new("RGB", size)
    px = img.load()
    for y in range(height):
        t = y / max(1, height - 1)
        row = tuple(int(top[i] * (1 - t) + bottom[i] * t) for i in range(3))
        for x in range(width):
            px[x, y] = row
    return img


def horizontal_gradient(size, left, right):
    width, height = size
    img = Image.new("RGB", size)
    px = img.load()
    for x in range(width):
        t = x / max(1, width - 1)
        col = tuple(int(left[i] * (1 - t) + right[i] * t) for i in range(3))
        for y in range(height):
            px[x, y] = col
    return img


def tint(color, delta):
    return tuple(max(0, min(255, c + delta)) for c in color)


def gradient_fill_from_mask(size, mask, top_color, bottom_color, horizontal=False, opacity=255):
    if horizontal:
        gradient = horizontal_gradient(size, top_color, bottom_color).convert("RGBA")
    else:
        gradient = vertical_gradient(size, top_color, bottom_color).convert("RGBA")
    if opacity != 255:
        alpha = Image.new("L", size, opacity)
        gradient.putalpha(alpha)
    cutout = Image.new("RGBA", size, (0, 0, 0, 0))
    cutout.paste(gradient, (0, 0), mask)
    return cutout


def add_soft_glow(img, xy, radius, color, alpha):
    layer = Image.new("RGBA", img.size, (0, 0, 0, 0))
    draw = ImageDraw.Draw(layer)
    x, y = xy
    draw.ellipse((x - radius, y - radius, x + radius, y + radius), fill=color + (alpha,))
    layer = layer.filter(ImageFilter.GaussianBlur(radius // 2))
    img.alpha_composite(layer)


def draw_sea_background(size, title=None, subtitle=None):
    base = vertical_gradient(size, PALETTE["sea_top"], PALETTE["sea_bottom"]).convert("RGBA")
    width, height = size
    draw = ImageDraw.Draw(base)

    for cx, cy, r, a in [
        (width * 0.18, height * 0.16, width * 0.22, 36),
        (width * 0.78, height * 0.22, width * 0.26, 26),
        (width * 0.52, height * 0.55, width * 0.34, 18),
    ]:
        add_soft_glow(base, (int(cx), int(cy)), int(r), (190, 250, 255), int(a))

    light_layer = Image.new("RGBA", size, (0, 0, 0, 0))
    ldraw = ImageDraw.Draw(light_layer)
    for x in [int(width * 0.18), int(width * 0.42), int(width * 0.66), int(width * 0.84)]:
        ldraw.polygon(
            [
                (x - 36, 0),
                (x + 36, 0),
                (x + 120, int(height * 0.72)),
                (x - 90, int(height * 0.72)),
            ],
            fill=(255, 255, 255, 20),
        )
    light_layer = light_layer.filter(ImageFilter.GaussianBlur(24))
    base.alpha_composite(light_layer)

    wave = Image.new("RGBA", size, (0, 0, 0, 0))
    wdraw = ImageDraw.Draw(wave)
    for idx, color in enumerate([(80, 211, 222, 45), (20, 103, 158, 70), (16, 81, 134, 110)]):
        top = int(height * (0.22 + idx * 0.18))
        points = []
        for x in range(-20, width + 21, 20):
            offset = ((x // 20) % 2) * 8
            points.append((x, top + offset))
        points += [(width, height), (0, height)]
        wdraw.polygon(points, fill=color)
    base.alpha_composite(wave)

    floor = ImageDraw.Draw(base)
    floor.ellipse((-40, height - 58, width + 40, height + 58), fill=(10, 52, 98, 220))
    floor.ellipse((15, height - 46, width - 15, height + 34), fill=(12, 71, 118, 210))

    ridge_layer = Image.new("RGBA", size, (0, 0, 0, 0))
    rdraw = ImageDraw.Draw(ridge_layer)
    ridge_points = [
        (0, height - 78),
        (int(width * 0.16), height - 92),
        (int(width * 0.3), height - 80),
        (int(width * 0.46), height - 104),
        (int(width * 0.63), height - 76),
        (int(width * 0.82), height - 96),
        (width, height - 82),
        (width, height),
        (0, height),
    ]
    rdraw.polygon(ridge_points, fill=(8, 49, 90, 190))
    base.alpha_composite(ridge_layer)

    plant_layer = Image.new("RGBA", size, (0, 0, 0, 0))
    pdraw = ImageDraw.Draw(plant_layer)
    stems = [24, 52, 74, width - 76, width - 48, width - 24, width // 2]
    for i, sx in enumerate(stems):
        color = PALETTE["leaf_1"] if i % 2 == 0 else PALETTE["leaf_2"]
        sway = -10 if i % 2 == 0 else 10
        pdraw.polygon(
            [
                (sx, height - 8),
                (sx + 10 + sway, height - 54),
                (sx + 18 + sway, height - 112),
                (sx + 8, height - 110),
                (sx - 2, height - 58),
            ],
            fill=color + (220,),
        )
        pdraw.polygon(
            [
                (sx + 6, height - 18),
                (sx + 20 - sway, height - 64),
                (sx + 26 - sway, height - 104),
                (sx + 14, height - 100),
                (sx + 2, height - 64),
            ],
            fill=color + (180,),
        )
    base.alpha_composite(plant_layer)

    coral_layer = Image.new("RGBA", size, (0, 0, 0, 0))
    cdraw = ImageDraw.Draw(coral_layer)
    coral_clusters = [
        (int(width * 0.12), height - 48, PALETTE["pink"]),
        (int(width * 0.72), height - 56, PALETTE["orange"]),
        (int(width * 0.88), height - 44, PALETTE["violet"]),
    ]
    for cx, cy, color in coral_clusters:
        for dx, h, w in [(-18, 34, 9), (-4, 52, 12), (12, 40, 10), (24, 30, 8)]:
            cdraw.rounded_rectangle((cx + dx, cy - h, cx + dx + w, cy), radius=w // 2, fill=color + (210,))
            cdraw.ellipse((cx + dx - 4, cy - h - 8, cx + dx + w + 4, cy - h + 6), fill=tint(color, 16) + (220,))
    coral_layer = coral_layer.filter(ImageFilter.GaussianBlur(0.4))
    base.alpha_composite(coral_layer)

    bubble_layer = Image.new("RGBA", size, (0, 0, 0, 0))
    bdraw = ImageDraw.Draw(bubble_layer)
    bubbles = [(28, 76, 8), (76, 42, 5), (192, 88, 7), (216, 56, 4), (170, 130, 6)]
    for x, y, r in bubbles:
        bdraw.ellipse((x - r, y - r, x + r, y + r), outline=(245, 253, 255, 180), width=2)
        bdraw.ellipse((x - r + 3, y - r + 3, x - r + 5, y - r + 5), fill=(255, 255, 255, 150))
    base.alpha_composite(bubble_layer)

    if title:
        text_draw = ImageDraw.Draw(base)
        text_draw.rounded_rectangle((18, 26, width - 18, 122), radius=24, fill=(8, 37, 74, 110))
        text_draw.text((24, 34), title, font=FONT_XL, fill=PALETTE["white"])
        if subtitle:
            text_draw.text((26, 82), subtitle, font=FONT_M, fill=(205, 244, 255))

    return base


def draw_game_background(size):
    base = draw_sea_background(size)
    width, height = size
    overlay = Image.new("RGBA", size, (0, 0, 0, 0))
    draw = ImageDraw.Draw(overlay)
    draw.rounded_rectangle((8, 8, width - 8, 38), radius=15, fill=(5, 28, 58, 110))
    base.alpha_composite(overlay)
    return base


def save(img, name, fmt=None):
    path = ASSET_DIR / name
    if fmt == "JPEG":
        img.convert("RGB").save(path, quality=92)
    else:
        img.save(path)


def draw_button_sheet():
    img = Image.new("RGBA", (80, 68), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    for idx, fill in enumerate([(34, 182, 190), (255, 184, 71)]):
        top = idx * 34
        draw.rounded_rectangle((2, top + 2, 78, top + 31), radius=15, fill=(8, 50, 88, 70))
        draw.rounded_rectangle((0, top, 76, top + 29), radius=15, fill=fill)
        draw.rounded_rectangle((0, top, 76, top + 15), radius=15, fill=tuple(min(255, c + 25) for c in fill))
    save(img, "menuitem.png")


def draw_menu_text():
    img = Image.new("RGBA", (55, 52), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    labels = ["START", "HELP", "SOON", "EXIT"]
    for i, label in enumerate(labels):
        y = i * 13
        bbox = draw.textbbox((0, 0), label, font=FONT_S)
        tw = bbox[2] - bbox[0]
        th = bbox[3] - bbox[1]
        draw.text(((55 - tw) / 2, y + (13 - th) / 2 - 1), label, font=FONT_S, fill=(10, 54, 88))
    save(img, "menutext.png")


def draw_joystick():
    outer = Image.new("RGBA", (55, 55), (0, 0, 0, 0))
    draw = ImageDraw.Draw(outer)
    draw.ellipse((1, 1, 53, 53), fill=(255, 255, 255, 32), outline=(255, 255, 255, 138), width=3)
    draw.ellipse((9, 9, 45, 45), outline=(133, 241, 232, 110), width=2)
    save(outer, "virjoy_outter.png")

    inner = Image.new("RGBA", (11, 11), (0, 0, 0, 0))
    draw = ImageDraw.Draw(inner)
    draw.ellipse((0, 0, 10, 10), fill=PALETTE["yellow"], outline=(255, 245, 220), width=1)
    save(inner, "virjoy_inner.png")


def make_bubbles():
    img = Image.new("RGBA", (170, 34), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    for i, radius in enumerate([6, 8, 10, 12, 14]):
        cx = i * 34 + 17
        cy = 17
        draw.ellipse((cx - radius, cy - radius, cx + radius, cy + radius), outline=(236, 252, 255, 220), width=2)
        draw.ellipse((cx - radius + 4, cy - radius + 4, cx - radius + 7, cy - radius + 7), fill=(255, 255, 255, 160))
    save(img, "airbubble.png")


def make_grass():
    img = Image.new("RGBA", (228, 62), (0, 0, 0, 0))
    for frame in range(3):
        tile = Image.new("RGBA", (76, 62), (0, 0, 0, 0))
        draw = ImageDraw.Draw(tile)
        for idx, base_x in enumerate([8, 20, 34, 48, 60]):
            sway = ((idx + frame) % 3 - 1) * 6
            color = PALETTE["leaf_1"] if idx % 2 == 0 else PALETTE["leaf_2"]
            draw.polygon(
                [(base_x, 60), (base_x + sway + 6, 26), (base_x + sway + 3, 4), (base_x - 4, 28)],
                fill=color + (230,),
            )
            draw.polygon(
                [(base_x + 3, 60), (base_x - sway + 8, 30), (base_x - sway + 10, 10), (base_x - 1, 32)],
                fill=tuple(max(0, c - 20) for c in color) + (200,),
            )
        img.alpha_composite(tile, (frame * 76, 0))
    save(img, "floatgrass.png")


def fish_frame(size, body, fin, accent, eye_dark, direction="right", mouth_open=False, turn=False, stripe=False):
    width, height = size
    canvas = Image.new("RGBA", size, (0, 0, 0, 0))
    draw = ImageDraw.Draw(canvas)
    shadow = (0, 0, 0, 55)

    if direction == "left":
        direction = "right"
        mirror_later = True
    else:
        mirror_later = False

    body_box = (int(width * 0.16), int(height * 0.18), int(width * 0.82), int(height * 0.84))
    if turn:
        body_box = (int(width * 0.24), int(height * 0.1), int(width * 0.72), int(height * 0.92))

    body_mask = Image.new("L", size, 0)
    bdraw = ImageDraw.Draw(body_mask)
    bdraw.ellipse(body_box, fill=255)
    nose = [
        (int(width * 0.72), int(height * 0.32)),
        (width - 1, int(height * 0.5)),
        (int(width * 0.72), int(height * 0.68)),
    ]
    bdraw.polygon(nose, fill=255)
    body_mask = body_mask.filter(ImageFilter.GaussianBlur(0.3))

    shadow_mask = body_mask.filter(ImageFilter.GaussianBlur(2))
    shadow_layer = Image.new("RGBA", size, (0, 0, 0, 0))
    shadow_layer.paste((0, 0, 0, 80), (2, 3), shadow_mask)
    canvas.alpha_composite(shadow_layer)

    body_layer = gradient_fill_from_mask(size, body_mask, tint(body, 24), tint(body, -18))
    canvas.alpha_composite(body_layer)

    belly_mask = Image.new("L", size, 0)
    beldraw = ImageDraw.Draw(belly_mask)
    beldraw.ellipse((int(width * 0.26), int(height * 0.44), int(width * 0.78), int(height * 0.88)), fill=210)
    belly_mask = ImageChops.multiply(belly_mask, body_mask)
    belly = gradient_fill_from_mask(size, belly_mask, tint(body, 12), tint(body, -36), opacity=120)
    canvas.alpha_composite(belly)

    highlight_mask = Image.new("L", size, 0)
    hdraw = ImageDraw.Draw(highlight_mask)
    hdraw.ellipse((int(width * 0.26), int(height * 0.18), int(width * 0.64), int(height * 0.44)), fill=200)
    highlight_mask = ImageChops.multiply(highlight_mask, body_mask)
    highlight = Image.new("RGBA", size, (255, 255, 255, 0))
    highlight.paste((255, 255, 255, 48), (0, 0), highlight_mask.filter(ImageFilter.GaussianBlur(3)))
    canvas.alpha_composite(highlight)

    tail_layer = Image.new("RGBA", size, (0, 0, 0, 0))
    tdraw = ImageDraw.Draw(tail_layer)
    upper_tail = [
        (int(width * 0.2), int(height * 0.49)),
        (0, int(height * 0.1)),
        (int(width * 0.06), int(height * 0.48)),
    ]
    lower_tail = [
        (int(width * 0.2), int(height * 0.53)),
        (0, int(height * 0.9)),
        (int(width * 0.06), int(height * 0.54)),
    ]
    mid_tail = [
        (int(width * 0.18), int(height * 0.48)),
        (int(width * 0.03), int(height * 0.5)),
        (int(width * 0.18), int(height * 0.56)),
    ]
    for poly, col in [(upper_tail, tint(fin, 22)), (lower_tail, tint(fin, -6)), (mid_tail, fin)]:
        tdraw.polygon([(x + 2, y + 2) for x, y in poly], fill=shadow)
        tdraw.polygon(poly, fill=col + (255,))
    canvas.alpha_composite(tail_layer)

    fin_layer = Image.new("RGBA", size, (0, 0, 0, 0))
    fdraw = ImageDraw.Draw(fin_layer)
    top_fin = [
        (int(width * 0.34), int(height * 0.24)),
        (int(width * 0.48), int(height * 0.02)),
        (int(width * 0.58), int(height * 0.32)),
    ]
    bottom_fin = [
        (int(width * 0.42), int(height * 0.7)),
        (int(width * 0.58), int(height * 0.98)),
        (int(width * 0.64), int(height * 0.7)),
    ]
    side_fin = [
        (int(width * 0.44), int(height * 0.48)),
        (int(width * 0.28), int(height * 0.64)),
        (int(width * 0.52), int(height * 0.66)),
    ]
    for poly, col, alpha in [
        (top_fin, tint(fin, 14), 225),
        (bottom_fin, tint(fin, -10), 210),
        (side_fin, tint(accent, 8), 180),
    ]:
        fdraw.polygon(poly, fill=col + (alpha,))
    fin_layer = fin_layer.filter(ImageFilter.GaussianBlur(0.3))
    canvas.alpha_composite(fin_layer)

    pattern_layer = Image.new("RGBA", size, (0, 0, 0, 0))
    pdraw = ImageDraw.Draw(pattern_layer)
    if stripe:
        for px in [0.36, 0.49, 0.61]:
            x = int(width * px)
            pdraw.rounded_rectangle(
                (x, int(height * 0.22), x + max(4, int(width * 0.08)), int(height * 0.8)),
                radius=max(2, int(width * 0.03)),
                fill=tint(accent, 10) + (165,),
            )
    else:
        pdraw.ellipse((int(width * 0.38), int(height * 0.28), int(width * 0.6), int(height * 0.74)), fill=tint(accent, 6) + (190,))
        for ox, oy, r in [
            (0.5, 0.36, 0.04),
            (0.56, 0.5, 0.032),
            (0.46, 0.58, 0.028),
        ]:
            cx, cy, rr = int(width * ox), int(height * oy), max(2, int(width * r))
            pdraw.ellipse((cx - rr, cy - rr, cx + rr, cy + rr), fill=tint(accent, 28) + (160,))
    canvas.alpha_composite(pattern_layer)

    gill_x = int(width * 0.66)
    draw.arc((gill_x - 8, int(height * 0.28), gill_x + 10, int(height * 0.74)), start=72, end=288, fill=tint(body, -55), width=max(1, width // 36))

    mouth_y = int(height * 0.52)
    if mouth_open:
        draw.polygon([(int(width * 0.84), mouth_y), (width - 1, mouth_y - 5), (width - 1, mouth_y + 5)], fill=tint(accent, 10))
    else:
        draw.polygon([(int(width * 0.84), mouth_y), (width - 2, mouth_y - 2), (width - 2, mouth_y + 2)], fill=tint(accent, 10))

    eye = (int(width * 0.68), int(height * 0.3), int(width * 0.82), int(height * 0.48))
    draw.ellipse(eye, fill=PALETTE["white"])
    pupil = (eye[0] + max(2, width // 22), eye[1] + max(2, height // 14), eye[0] + max(6, width // 10), eye[1] + max(6, height // 6))
    draw.ellipse(pupil, fill=eye_dark)
    draw.ellipse((eye[0] + 1, eye[1] + 1, eye[0] + max(3, width // 18), eye[1] + max(3, height // 12)), fill=(255, 255, 255, 180))
    draw.ellipse((eye[0] - 1, eye[1] - 1, eye[2] + 1, eye[3] + 1), outline=tint(eye_dark, -18), width=1)

    outline_mask = body_mask.filter(ImageFilter.MaxFilter(5))
    edge = ImageChops.subtract(outline_mask, body_mask)
    outline = Image.new("RGBA", size, (0, 0, 0, 0))
    outline.paste(tint(eye_dark, -8) + (110,), (0, 0), edge)
    canvas = Image.alpha_composite(outline, canvas)

    if mirror_later:
        canvas = ImageOps.mirror(canvas)
    return canvas


def fish_sheet(frame_size, colors, stripe=False):
    fw, fh = frame_size
    sheet = Image.new("RGBA", (fw * 12, fh), (0, 0, 0, 0))
    frames = [
        fish_frame(frame_size, *colors, direction="right", stripe=stripe),
        fish_frame(frame_size, *colors, direction="right", stripe=stripe),
        fish_frame(frame_size, *colors, direction="right", stripe=stripe),
        fish_frame(frame_size, *colors, direction="left", stripe=stripe),
        fish_frame(frame_size, *colors, direction="left", stripe=stripe),
        fish_frame(frame_size, *colors, direction="left", stripe=stripe),
        fish_frame(frame_size, *colors, direction="right", turn=True, stripe=stripe),
        fish_frame(frame_size, *colors, direction="left", turn=True, stripe=stripe),
        fish_frame(frame_size, *colors, direction="right", mouth_open=True, stripe=stripe),
        fish_frame(frame_size, *colors, direction="right", mouth_open=True, stripe=stripe),
        fish_frame(frame_size, *colors, direction="left", mouth_open=True, stripe=stripe),
        fish_frame(frame_size, *colors, direction="left", mouth_open=True, stripe=stripe),
    ]
    for idx, frame in enumerate(frames):
        sheet.alpha_composite(frame, (idx * fw, 0))
    return sheet


def draw_banner(size, text, fill, accent=None):
    img = Image.new("RGBA", size, (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    width, height = size
    accent = accent or tuple(min(255, c + 24) for c in fill[:3])
    draw.rounded_rectangle((3, 4, width - 3, height - 1), radius=height // 2, fill=(6, 39, 69, 90))
    draw.rounded_rectangle((0, 0, width - 6, height - 6), radius=(height - 6) // 2, fill=fill)
    draw.rounded_rectangle((0, 0, width - 6, height // 2), radius=(height - 6) // 2, fill=accent)
    draw.rounded_rectangle((8, 5, width - 18, height // 2), radius=(height - 10) // 2, fill=(255, 255, 255, 34))
    bbox = draw.textbbox((0, 0), text, font=FONT_M)
    tw = bbox[2] - bbox[0]
    th = bbox[3] - bbox[1]
    draw.text(((width - 6 - tw) / 2, (height - 6 - th) / 2 - 1), text, font=FONT_M, fill=(14, 54, 86))
    return img


def draw_run_frame(index):
    img = Image.new("RGBA", (72, 97), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    offset = [-4, -2, 0, 2, 4][index]
    draw.ellipse((12, 18, 60, 66), fill=PALETTE["yellow"])
    draw.polygon([(14, 42), (2, 30 + offset), (4, 60 + offset)], fill=PALETTE["orange"])
    draw.polygon([(26, 58), (18, 84), (32, 70)], fill=PALETTE["orange"])
    draw.polygon([(42, 58), (54, 86 - offset), (50, 66)], fill=PALETTE["orange"])
    draw.ellipse((38, 26, 52, 40), fill=PALETTE["white"])
    draw.ellipse((44, 30, 49, 35), fill=PALETTE["shadow"])
    return img


def generate():
    save(draw_game_background((1280, 720)), "background.png")
    save(draw_sea_background((1280, 720), "HOW TO PLAY", "Swipe, eat, escape"), "bgimg7.jpg", fmt="JPEG")
    save(draw_sea_background((1280, 720), "DEEP BITE", "Modern ocean arcade"), "logo.png")
    save(draw_sea_background((1600, 900), "DEEP BITE", "Modern flat art pack"), "welcome.png")
    save(draw_sea_background((1280, 720), "OCEAN ARCADE", "A cleaner mobile-game take"), "menu.png")

    draw_button_sheet()
    draw_menu_text()
    draw_joystick()
    make_bubbles()
    make_grass()

    save(fish_sheet((42, 24), ((90, 229, 216), (28, 168, 180), (214, 251, 247), (12, 48, 92)), stripe=False), "suergeonfish.png")
    save(fish_sheet((78, 40), ((255, 199, 98), (255, 132, 84), (255, 237, 182), (18, 60, 108)), stripe=True), "tuna.png")
    save(fish_sheet((110, 86), ((136, 139, 244), (89, 94, 208), (205, 212, 255), (17, 50, 94)), stripe=False), "lion.png")
    save(fish_sheet((220, 96), ((102, 120, 155), (63, 82, 123), (200, 212, 228), (8, 34, 72)), stripe=False), "shark.png")

    save(fish_sheet((34, 28), ((255, 198, 77), (255, 131, 85), (255, 244, 196), (17, 52, 97)), stripe=True), "angelfishnormal.png")
    save(fish_sheet((84, 68), ((255, 203, 84), (255, 116, 82), (255, 244, 196), (17, 52, 97)), stripe=True), "angelfishbig.png")
    save(fish_sheet((118, 94), ((255, 212, 103), (255, 112, 98), (255, 248, 214), (17, 52, 97)), stripe=True), "angelfishsuper.png")

    save(fish_sheet((13, 12), ((255, 198, 77), (255, 131, 85), (255, 244, 196), (17, 52, 97)), stripe=True).resize((165, 12), Image.Resampling.LANCZOS), "myfishsmall.png")
    save(fish_sheet((18, 17), ((255, 198, 77), (255, 131, 85), (255, 244, 196), (17, 52, 97)), stripe=True).resize((220, 17), Image.Resampling.LANCZOS), "myfishnormal.png")
    save(fish_sheet((34, 28), ((255, 203, 84), (255, 116, 82), (255, 244, 196), (17, 52, 97)), stripe=True).resize((407, 28), Image.Resampling.LANCZOS), "myfishbig.png")

    save(draw_banner((240, 30), "GAME OVER", (255, 116, 98)), "gameover.png")
    save(draw_banner((170, 34), "OOPS", (255, 196, 87)), "sorry.png")
    save(draw_banner((100, 25), "CLEAR", (82, 224, 210)), "pass.png")

    for i in range(5):
        save(draw_run_frame(i), f"run_anim{i + 1}.png")


if __name__ == "__main__":
    generate()
