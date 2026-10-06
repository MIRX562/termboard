package com.mirxlab.termboard.ui.emoji

data class EmojiCategory(
    val name: String,
    val icon: String,
    val emojis: List<String>
)

object EmojiRepository {

    val searchTerms = mapOf(
        "😀" to "grinning smile happy face",
        "😂" to "laugh crying joy tears",
        "🥰" to "love hearts smiling",
        "😍" to "love heart eyes",
        "🤔" to "think thinking question",
        "😎" to "cool sunglasses",
        "😭" to "cry sob sad tears",
        "😊" to "smile happy blush",
        "👍" to "thumbs up like approve",
        "👎" to "thumbs down dislike",
        "🙏" to "pray please thanks",
        "❤️" to "heart love red",
        "🐶" to "dog puppy",
        "🐱" to "cat kitten",
        "🐻" to "bear",
        "🦊" to "fox",
        "🐸" to "frog",
        "🦄" to "unicorn",
        "🍎" to "apple fruit",
        "🍌" to "banana fruit",
        "🍕" to "pizza food",
        "🍔" to "burger hamburger food",
        "☕" to "coffee drink",
        "🚗" to "car vehicle",
        "✈️" to "airplane plane travel",
        "🚀" to "rocket space",
        "💻" to "laptop computer technology",
        "📱" to "phone mobile",
        "⚽" to "soccer football sport",
        "🎉" to "party celebration",
        "🔥" to "fire hot",
        "⭐" to "star",
        "🌈" to "rainbow",
        "☀️" to "sun sunny weather",
        "🌧" to "rain weather",
        "✅" to "check done yes",
        "❌" to "cross no wrong",
        "💡" to "idea light bulb"
    )

    val categories: List<EmojiCategory> = listOf(
        EmojiCategory(
            name = "Smileys",
            icon = "😀",
            emojis = listOf(
                "😀", "😃", "😄", "😁", "😆", "😅", "🤣", "😂", "🙂", "🙃",
                "😉", "😊", "😇", "🥰", "😍", "🤩", "😘", "😗", "😚", "😙",
                "😋", "😛", "😜", "🤪", "😝", "🤑", "🤗", "🤭", "🤫", "🤔",
                "🤐", "🤨", "😐", "😑", "😶", "😏", "😒", "🙄", "😬", "🤥",
                "😌", "😔", "😪", "🤤", "😴", "😷", "🤒", "🤕", "🤢", "🤮",
                "🤧", "🥵", "🥶", "🥴", "😵", "🤯", "🤠", "🥳", "😎", "🤓",
                "🧐", "😕", "😟", "🙁", "😮", "😯", "😲", "😳", "🥺", "😦", "😭"
            )
        ),
        EmojiCategory(
            name = "Gestures",
            icon = "👍",
            emojis = listOf(
                "👋", "🤚", "🖐", "✋", "🖖", "👌", "🤏", "✌️", "🤞", "🤟",
                "🤘", "🤙", "👈", "👉", "👆", "🖕", "👇", "☝️", "👍", "👎",
                "✊", "👊", "🤛", "🤜", "👏", "🙌", "👐", "🤲", "🤝", "🙏",
                "✍️", "💅", "🤳", "💪", "🦾", "🦿", "🦵", "🦶", "👂", "🦻"
            )
        ),
        EmojiCategory(
            name = "Animals",
            icon = "🐶",
            emojis = listOf(
                "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯",
                "🦁", "🐮", "🐷", "🐸", "🐵", "🙈", "🙉", "🙊", "🐒", "🐔",
                "🐧", "🐦", "🐤", "🐣", "🐥", "🦆", "🦅", "🦉", "🦇", "🐺",
                "🐗", "🐴", "🦄", "🐝", "🐛", "🦋", "🐌", "🐞", "🐜", "🦟"
            )
        ),
        EmojiCategory(
            name = "Food",
            icon = "🍎",
            emojis = listOf(
                "🍏", "🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🫐",
                "🍈", "🍒", "🍑", "🥭", "🍍", "🥥", "🥝", "🍅", "🍆", "🥑",
                "🥦", "🥬", "🥒", "🌶", "🫑", "🌽", "🥕", "🫒", "🧄", "🧅",
                "🥔", "🍠", "🥐", "🥯", "🍞", "🥖", "🥨", "🧀", "🥚", "🍳", "🍕", "🍔", "☕"
            )
        ),
        EmojiCategory(
            name = "Travel",
            icon = "🚗",
            emojis = listOf(
                "🚗", "🚕", "🚙", "🚌", "🚎", "🏎", "🚓", "🚑", "🚒", "🚐",
                "🛻", "🚚", "🚛", "🚜", "🦯", "🦽", "🦼", "🛴", "🚲", "🛵",
                "🏍", "🛺", "🚨", "🚔", "🚍", "🚘", "🚖", "🚡", "🚠", "🎛",
                "✈️", "🛫", "🛬", "🪂", "💺", "🚁", "🚟", "🚠", "🚡", "🚀"
            )
        ),
        EmojiCategory(
            name = "Objects",
            icon = "💡",
            emojis = listOf(
                "⌚️", "📱", "📲", "💻", "⌨️", "🖥", "🖨", "🖱", "🖲", "🕹",
                "🗜", "💽", "💾", "💿", "📀", "📼", "📷", "📸", "📹", "🎥",
                "📽", "🎞", "📞", "☎️", "📟", "📠", "📺", "📻", "🎙", "🎚",
                "🎛", "🧭", "⏱", "⏲", "⏰", "🕰", "⌛️", "⏳", "📡", "🔋"
            )
        ),
        EmojiCategory(
            name = "Symbols",
            icon = "❤️",
            emojis = listOf(
                "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔",
                "❣️", "💕", "💞", "💓", "💗", "💖", "💘", "💝", "💟", "☮️",
                "✝️", "☪️", "🕉", "☸️", "✡️", "🔯", "🕎", "☯️", "☦️", "🛐",
                "⛎", "♈️", "♉️", "♊️", "♋️", "♌️", "♍️", "♎️", "♏️", "♐️",
                "🔥", "⭐", "🌈", "☀️", "🌧", "✅", "❌", "🎉", "⚽"
            )
        )
    )
}
