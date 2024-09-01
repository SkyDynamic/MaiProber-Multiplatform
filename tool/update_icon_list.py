import json

from httpx import AsyncClient

client = AsyncClient()

async def update_icon_id():
    song_info = []
    result = await client.get("https://maimai.lxns.net/api/v0/maimai/icon/list")
    for icon in result.json()["icons"]:
        song_info.append(icon["id"])

    with open("./icon_list.json", "w", encoding="utf-8") as f:
        json.dump(song_info, f, ensure_ascii=False)

if __name__ == "__main__":
    import asyncio
    asyncio.run(update_icon_id())
