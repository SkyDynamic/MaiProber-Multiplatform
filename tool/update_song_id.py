import json

from httpx import AsyncClient

client = AsyncClient()

async def update_song_id():
    song_info = []
    result = await client.get("https://maimai.lxns.net/api/v0/maimai/song/list")
    for song in result.json()["songs"]:
        dx_difficulties = song["difficulties"]["dx"]
        standard_difficulties = song["difficulties"]["standard"]
        dx_levels = [difficulty["level_value"] for difficulty in dx_difficulties]
        standard_levels = [difficulty["level_value"] for difficulty in standard_difficulties]

        song_info.append(
            {
                "id": song["id"],
                "title": song["title"],
                "version": song["version"],
                "dx_levels": dx_levels,
                "standard_levels": standard_levels,
            }
        )

    with open("./song_info.json", "w", encoding="utf-8") as f:
        json.dump(song_info, f, ensure_ascii=False)

if __name__ == "__main__":
    import asyncio
    asyncio.run(update_song_id())
