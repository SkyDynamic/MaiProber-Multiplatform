from httpx import AsyncClient

client = AsyncClient()

async def download_maimai_dan():
    for i in range(1, 24):
        result = await client.get(f"https://maimai.lxns.net/assets/maimai/course_rank/{i}.webp")

        with open(f"./{i}.webp", "wb") as f:
            f.write(result.content)

if __name__ == "__main__":
    import asyncio
    asyncio.run(download_maimai_dan())
