# Extends the official Lavalink image with yt-dlp support, required by LavaSrc's
# ytdlp source (see application.yml: plugins.lavasrc.sources.ytdlp).
# yt-dlp is used to bypass YouTube's InnerTube/SABR playback restrictions that
# currently break the dev.lavalink.youtube plugin (see application.yml comments).
FROM ghcr.io/lavalink-devs/lavalink:4-alpine

USER root

# ffmpeg is bundled in case yt-dlp needs it for format post-processing;
# yt-dlp itself is installed into an isolated venv to avoid conflicting with
# Alpine's externally-managed Python environment, then symlinked onto PATH.
RUN apk add --no-cache ffmpeg py3-pip python3   \
    && python3 -m venv /opt/yt-dlp-venv \
    && /opt/yt-dlp-venv/bin/pip install --no-cache-dir --upgrade yt-dlp \
    && ln -s /opt/yt-dlp-venv/bin/yt-dlp /usr/local/bin/yt-dlp \
    && chmod -R a+rX /opt/yt-dlp-venv

USER 322:322
