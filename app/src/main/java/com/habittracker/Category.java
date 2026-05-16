package com.habittracker;

public class Category {
    private final String name;
    private final String emoji;
    private final int color; // ARGB int

    public Category(String name, String emoji, int color) {
        this.name = name;
        this.emoji = emoji;
        this.color = color;
    }

    public String getName() { return name; }
    public String getEmoji() { return emoji; }
    public int getColor() { return color; }

    public static java.util.List<Category> getDefaultCategories() {
        java.util.List<Category> list = new java.util.ArrayList<>();
        list.add(new Category("Abbandona una cattiva abitudine", "🚫", 0xFFD32F2F));
        list.add(new Category("Arte", "🎨", 0xFFE91E63));
        list.add(new Category("Meditazione", "🧘", 0xFF9C27B0));
        list.add(new Category("Apprendimento", "📚", 0xFF673AB7));
        list.add(new Category("Sport", "🚴", 0xFF2196F3));
        list.add(new Category("Divertimento", "⭐", 0xFF00BCD4));
        list.add(new Category("Sociale", "💬", 0xFF009688));
        list.add(new Category("Finanza", "💲", 0xFF4CAF50));
        list.add(new Category("Salute", "🏥", 0xFF388E3C));
        list.add(new Category("Lavoro", "💼", 0xFF558B2F));
        list.add(new Category("Nutrizione", "🍴", 0xFFFF9800));
        list.add(new Category("Casa", "🏠", 0xFFFF6F00));
        list.add(new Category("All'aperto", "🏔️", 0xFFF57C00));
        list.add(new Category("Varie", "📦", 0xFFFF5722));
        return list;
    }
}
