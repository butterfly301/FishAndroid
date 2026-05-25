from pathlib import Path
import shutil

from PIL import Image


ROOT = Path(r"E:\FishAndroid")
SOURCE_DIR = ROOT / "assets"
TARGET_DIR = ROOT / "app" / "src" / "main" / "assets"


SPRITE_TARGETS = {
    "suergeonfish.png": (42, 24),
    "tuna.png": (78, 40),
    "lion.png": (110, 86),
    "shark.png": (220, 96),
    "angelfishnormal.png": (34, 28),
    "angelfishbig.png": (84, 68),
    "angelfishsuper.png": (118, 94),
}

BACKGROUND_TARGETS = {
    "background.png": (1280, 720),
    "menu.png": (1280, 720),
    "logo.png": (1280, 720),
    "bgimg7.jpg": (1280, 720),
    "welcome.png": (1600, 900),
}

DIRECT_COPY = [
    "airbubble.png",
    "floatgrass.png",
    "gameover.png",
    "menuitem.png",
    "menutext.png",
    "myfishbig.png",
    "myfishnormal.png",
    "myfishsmall.png",
    "pass.png",
    "run_anim1.png",
    "run_anim2.png",
    "run_anim3.png",
    "run_anim4.png",
    "run_anim5.png",
    "select.mid",
    "sorry.png",
    "virjoy_inner.png",
    "virjoy_outter.png",
    "backgoundsound.mid",
]


def upscale_sprite_sheet(name: str, target_frame_size: tuple[int, int]) -> None:
    source_path = SOURCE_DIR / name
    target_path = TARGET_DIR / name
    source = Image.open(source_path).convert("RGBA")
    frame_count = 12
    frame_width = source.width // frame_count
    frame_height = source.height
    target_width, target_height = target_frame_size
    sheet = Image.new("RGBA", (target_width * frame_count, target_height), (0, 0, 0, 0))

    for index in range(frame_count):
        frame = source.crop((index * frame_width, 0, (index + 1) * frame_width, frame_height))
        frame = frame.resize((target_width, target_height), Image.Resampling.LANCZOS)
        sheet.alpha_composite(frame, (index * target_width, 0))

    sheet.save(target_path)


def upscale_fullscreen(name: str, target_size: tuple[int, int]) -> None:
    source_path = SOURCE_DIR / name
    target_path = TARGET_DIR / name
    image = Image.open(source_path)
    if target_path.suffix.lower() in [".jpg", ".jpeg"]:
        image = image.convert("RGB")
    else:
        image = image.convert("RGBA")
    image = image.resize(target_size, Image.Resampling.LANCZOS)
    if target_path.suffix.lower() in [".jpg", ".jpeg"]:
        image.save(target_path, quality=92)
    else:
        image.save(target_path)


def copy_direct(name: str) -> None:
    shutil.copy2(SOURCE_DIR / name, TARGET_DIR / name)


def main() -> None:
    for name, size in SPRITE_TARGETS.items():
        upscale_sprite_sheet(name, size)

    for name, size in BACKGROUND_TARGETS.items():
        upscale_fullscreen(name, size)

    for name in DIRECT_COPY:
        copy_direct(name)


if __name__ == "__main__":
    main()
