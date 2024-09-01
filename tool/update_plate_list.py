import json

from httpx import AsyncClient

client = AsyncClient()

async def update_song_id():
    song_info = []
    result = await client.get("https://maimai.lxns.net/api/v0/maimai/plate/list")
    for icon in result.json()["plates"]:
        song_info.append(icon["id"])

    with open("./plate_list.json", "w", encoding="utf-8") as f:
        json.dump(song_info, f, ensure_ascii=False)

if __name__ == "__main__":
    import asyncio
    asyncio.run(update_song_id())