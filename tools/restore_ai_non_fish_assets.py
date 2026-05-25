from pathlib import Path

from PIL import Image, ImageDraw, ImageEnhance, ImageFilter, ImageFont


ROOT = Path(r"E:\FishAndroid")
ASSET_DIR = ROOT / "app" / "src" / "main" / "assets"
GEN_DIR = Path(r"C:\Users\wukaidong\.codex\generated_images\019e4935-8ad4-7e63-b0e6-df30c6226c56")

PLAY_BACKGROUND = GEN_DIR / "ig_09e16d5ccd6a23ef016a0fb63f671c81918fa21d85d6e0be4f.png"
MENU_BACKGROUND = GEN_DIR / "ig_09e16d5ccd6a23ef016a0fb687d1488191869a8976c5d8e78b.png"
UI_KIT = GEN_DIR / "ig_087cbb3bb9c262ab016a0fae17910881919baa2353bc92f008.png"


def load_font(size: int, bold: bool = False) -> ImageFont.FreeTypeFont | ImageFont.ImageFont:
    candidates = []
    if bold:
        candidates.extend(
            [
                r"C:\Windows\Fonts\segoeuib.ttf",
                r"C:\Windows\Fonts\arialbd.ttf",
                r"C:\Windows\Fonts\msyhbd.ttc",
            ]
        )
    candidates.extend(
        [
            r"C:\Windows\Fonts\segoeui.ttf",
            r"C:\Windows\Fonts\arial.ttf",
            r"C:\Windows\Fonts\msyh.ttc",
        ]
    )
    for font_path in candidates:
        path = Path(font_path)
        if path.exists():
            return ImageFont.truetype(path.as_posix(), size)
    return ImageFont.load_default()


TITLE_FONT = load_font(96, bold=True)
SUBTITLE_FONT = load_font(34, bold=True)


def cover_resize(source_path: Path, target_size: tuple[int, int]) -> Image.Image:
    image = Image.open(source_path).convert("RGBA")
    scale = max(target_size[0] / image.width, target_size[1] / image.height)
    resized = image.resize(
        (int(image.width * scale), int(image.height * scale)),
        Image.Resampling.LANCZOS,
    )
    left = (resized.width - target_size[0]) // 2
    top = (resized.height - target_size[1]) // 2
    return resized.crop((left, top, left + target_size[0], top + target_size[1]))


def add_vignette(image: Image.Image, darkness: int = 60) -> Image.Image:
    overlay = Image.new("RGBA", image.size, (0, 0, 0, 0))
    draw = ImageDraw.Draw(overlay)
    width, height = image.size
    draw.rectangle((0, 0, width, height), fill=(0, 0, 0, darkness))
    overlay = overlay.filter(ImageFilter.GaussianBlur(48))
    return Image.alpha_composite(image, overlay)


def add_top_card(image: Image.Image, title: str, subtitle: str | None = None) -> Image.Image:
    composed = image.copy()
    draw = ImageDraw.Draw(composed)
    width, _ = composed.size
    draw.rounded_rectangle((104, 58, width - 104, 236), radius=38, fill=(7, 33, 71, 150))
    bbox = draw.textbbox((0, 0), title, font=TITLE_FONT)
    title_width = bbox[2] - bbox[0]
    draw.text(((width - title_width) / 2, 82), title, font=TITLE_FONT, fill=(255, 255, 255, 245))
    if subtitle:
        sbbox = draw.textbbox((0, 0), subtitle, font=SUBTITLE_FONT)
        subtitle_width = sbbox[2] - sbbox[0]
        draw.text(((width - subtitle_width) / 2, 176), subtitle, font=SUBTITLE_FONT, fill=(217, 244, 255, 220))
    return composed


def save_rgb(image: Image.Image, name: str, quality: int = 95) -> None:
    image.convert("RGB").save(ASSET_DIR / name, quality=quality)


def save_rgba(image: Image.Image, name: str) -> None:
    image.save(ASSET_DIR / name)


def crop_ui(box: tuple[int, int, int, int], target_size: tuple[int, int], alpha: bool = True) -> Image.Image:
    source = Image.open(UI_KIT).convert("RGBA")
    cropped = source.crop(box)
    resized = cropped.resize(target_size, Image.Resampling.LANCZOS)
    if alpha:
        return resized
    return resized.convert("RGB")


def restore_backgrounds() -> None:
    play = cover_resize(PLAY_BACKGROUND, (1280, 720))
    play = ImageEnhance.Color(play).enhance(1.05)
    save_rgba(play, "background.png")

    menu = cover_resize(MENU_BACKGROUND, (1280, 720))
    menu = add_vignette(menu, darkness=24)
    save_rgba(menu, "menu.png")

    logo = add_top_card(menu, "深海大作战", "海底吞食冒险")
    save_rgba(logo, "logo.png")

    welcome = cover_resize(MENU_BACKGROUND, (1600, 900))
    welcome = add_top_card(welcome, "深海大作战", "横屏版")
    save_rgba(welcome, "welcome.png")

    help_bg = cover_resize(PLAY_BACKGROUND, (1280, 720))
    help_bg = add_vignette(help_bg, darkness=36)
    save_rgb(help_bg, "bgimg7.jpg", quality=92)


def restore_ui_overlays() -> None:
    # Extract the glossy joystick art from the existing AI UI kit and resize to
    # the sizes expected by the current game logic, so we improve visuals
    # without changing control math or touching fish sprites.
    save_rgba(crop_ui((1088, 386, 1382, 679), (96, 96)), "virjoy_outter.png")
    save_rgba(crop_ui((1148, 450, 1316, 618), (48, 48)), "virjoy_inner.png")


def main() -> None:
    restore_backgrounds()
    restore_ui_overlays()


if __name__ == "__main__":
    main()
