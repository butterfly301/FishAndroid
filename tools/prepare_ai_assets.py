from pathlib import Path

from PIL import Image


ROOT = Path(r"E:\FishAndroid")
ASSET_DIR = ROOT / "app" / "src" / "main" / "assets"
GEN_DIR = Path(r"C:\Users\wukaidong\.codex\generated_images\019e4935-8ad4-7e63-b0e6-df30c6226c56")

FISH_SOURCES = {
    "player": GEN_DIR / "ig_09e16d5ccd6a23ef016a0fb531fd2c8191bd58603ea826035d.png",
    "small": GEN_DIR / "ig_09e16d5ccd6a23ef016a0fb55e85a08191b9955bd92d4be278.png",
    "big": GEN_DIR / "ig_09e16d5ccd6a23ef016a0fb58d7680819199ac3119ea17ce33.png",
    "shark": GEN_DIR / "ig_09e16d5ccd6a23ef016a0fb5b829808191b4d775d3846301f4.png",
}

BACKGROUND_SOURCES = {
    "play": GEN_DIR / "ig_09e16d5ccd6a23ef016a0fb63f671c81918fa21d85d6e0be4f.png",
    "menu": GEN_DIR / "ig_09e16d5ccd6a23ef016a0fb687d1488191869a8976c5d8e78b.png",
}


SPRITE_PLANS = {
    "player": {
        "source": FISH_SOURCES["player"],
        "right_frames": [0, 1, 2],
        "right_swerve": 3,
        "right_eat": [3, 4],
        "source_faces": "right",
    },
    "small": {
        "source": FISH_SOURCES["small"],
        "right_frames": [0, 1, 2],
        "right_swerve": 3,
        "right_eat": [4, 5],
        "source_faces": "right",
    },
    "big": {
        "source": FISH_SOURCES["big"],
        "right_frames": [0, 1, 2],
        "right_swerve": 3,
        "right_eat": [3, 4],
        "source_faces": "right",
    },
    "shark": {
        "source": FISH_SOURCES["shark"],
        "right_frames": [0, 1, 2],
        "right_swerve": 3,
        "right_eat": [3, 4],
        "source_faces": "right",
    },
}


def green_to_alpha(image: Image.Image) -> Image.Image:
    rgba = image.convert("RGBA")
    out = []
    for r, g, b, _ in list(rgba.getdata()):
        green_bias = g - max(r, b)
        distance = max(abs(r - 0), abs(g - 255), abs(b - 0))
        if green_bias > 80 and distance < 120:
            alpha = int(max(0, min(255, (distance - 18) * 255 / 80)))
            out.append((r, g, b, alpha))
        else:
            out.append((r, g, b, 255))
    rgba.putdata(out)
    return rgba


def crop_visible(image: Image.Image) -> Image.Image:
    alpha = image.getchannel("A")
    bbox = alpha.point(lambda value: 255 if value > 12 else 0).getbbox()
    if bbox is None:
        return image
    return image.crop(bbox)


def extract_sprites(source_path: Path) -> list[Image.Image]:
    rgba = green_to_alpha(Image.open(source_path).convert("RGBA"))
    alpha = rgba.getchannel("A")
    width, height = rgba.size
    columns = []
    for x in range(width):
        has_pixel = False
        for y in range(height):
            if alpha.getpixel((x, y)) > 12:
                has_pixel = True
                break
        columns.append(has_pixel)

    ranges = []
    start = None
    for x, occupied in enumerate(columns):
        if occupied and start is None:
            start = x
        elif not occupied and start is not None:
            ranges.append((start, x))
            start = None
    if start is not None:
        ranges.append((start, width))

    sprites = []
    for left, right in ranges:
        if right - left < 8:
            continue
        cropped = rgba.crop((left, 0, right, height))
        cropped = crop_visible(cropped)
        if cropped.width > 4 and cropped.height > 4:
            sprites.append(cropped)
    return sprites


def fit_center(image: Image.Image, target_size: tuple[int, int], scale: float = 0.9, y_bias: float = 0.55) -> Image.Image:
    target_w, target_h = target_size
    canvas = Image.new("RGBA", target_size, (0, 0, 0, 0))
    img_w, img_h = image.size
    ratio = min((target_w * scale) / img_w, (target_h * scale) / img_h)
    new_size = (max(1, int(img_w * ratio)), max(1, int(img_h * ratio)))
    resized = image.resize(new_size, Image.Resampling.LANCZOS)
    x = (target_w - new_size[0]) // 2
    y = int((target_h - new_size[1]) * y_bias)
    canvas.alpha_composite(resized, (x, y))
    return canvas


def extract_frame(sprite_images: list[Image.Image], source_index: int, target_size: tuple[int, int], scale: float, mirror: bool = False) -> Image.Image:
    frame = sprite_images[source_index].copy()
    if mirror:
        frame = frame.transpose(Image.Transpose.FLIP_LEFT_RIGHT)
    return fit_center(frame, target_size, scale=scale)


def build_sheet(plan_name: str, out_name: str, frame_size: tuple[int, int], scale: float = 0.92) -> None:
    plan = SPRITE_PLANS[plan_name]
    sprites = extract_sprites(plan["source"])
    sheet = Image.new("RGBA", (frame_size[0] * 12, frame_size[1]), (0, 0, 0, 0))
    if len(sprites) < 6:
        raise ValueError(f"Not enough sprites extracted from {plan['source']}")

    source_faces_right = plan["source_faces"] == "right"
    right_frames = [extract_frame(sprites, idx, frame_size, scale, mirror=not source_faces_right) for idx in plan["right_frames"]]
    left_frames = [frame.transpose(Image.Transpose.FLIP_LEFT_RIGHT) for frame in right_frames]
    right_swerve = extract_frame(sprites, plan["right_swerve"], frame_size, scale, mirror=not source_faces_right)
    left_swerve = right_swerve.transpose(Image.Transpose.FLIP_LEFT_RIGHT)
    right_eat = [extract_frame(sprites, idx, frame_size, scale, mirror=not source_faces_right) for idx in plan["right_eat"]]
    left_eat = [frame.transpose(Image.Transpose.FLIP_LEFT_RIGHT) for frame in right_eat]

    output_frames = [
        right_frames[0], right_frames[1], right_frames[2],
        left_frames[0], left_frames[1], left_frames[2],
        right_swerve, left_swerve,
        right_eat[0], right_eat[1],
        left_eat[0], left_eat[1],
    ]

    for index, frame in enumerate(output_frames):
        sheet.alpha_composite(frame, (index * frame_size[0], 0))
    sheet.save(ASSET_DIR / out_name)


def cover_resize(source_path: Path, out_name: str, target_size: tuple[int, int], fmt: str | None = None) -> None:
    image = Image.open(source_path).convert("RGB")
    scale = max(target_size[0] / image.width, target_size[1] / image.height)
    resized = image.resize((int(image.width * scale), int(image.height * scale)), Image.Resampling.LANCZOS)
    left = (resized.width - target_size[0]) // 2
    top = (resized.height - target_size[1]) // 2
    cropped = resized.crop((left, top, left + target_size[0], top + target_size[1]))
    if fmt == "JPEG":
        cropped.save(ASSET_DIR / out_name, quality=92)
    else:
        cropped.save(ASSET_DIR / out_name)


def main() -> None:
    build_sheet("small", "suergeonfish.png", (42, 24), scale=0.96)
    build_sheet("player", "tuna.png", (78, 40), scale=0.93)
    build_sheet("big", "lion.png", (110, 86), scale=0.93)
    build_sheet("shark", "shark.png", (220, 96), scale=0.94)

    build_sheet("player", "angelfishnormal.png", (34, 28), scale=0.92)
    build_sheet("player", "angelfishbig.png", (84, 68), scale=0.93)
    build_sheet("player", "angelfishsuper.png", (118, 94), scale=0.94)

    cover_resize(BACKGROUND_SOURCES["play"], "background.png", (1280, 720))
    cover_resize(BACKGROUND_SOURCES["menu"], "menu.png", (1280, 720))
    cover_resize(BACKGROUND_SOURCES["menu"], "logo.png", (1280, 720))
    cover_resize(BACKGROUND_SOURCES["menu"], "welcome.png", (1600, 900))
    cover_resize(BACKGROUND_SOURCES["menu"], "bgimg7.jpg", (1280, 720), fmt="JPEG")


if __name__ == "__main__":
    main()
