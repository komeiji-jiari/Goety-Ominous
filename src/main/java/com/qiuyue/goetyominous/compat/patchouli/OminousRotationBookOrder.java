package com.qiuyue.goetyominous.compat.patchouli;

import com.qiuyue.goetyominous.client.OminousIconRotation;
import net.minecraft.resources.ResourceLocation;
import vazkii.patchouli.client.book.BookCategory;
import vazkii.patchouli.client.book.BookContents;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OminousRotationBookOrder {

    private static final ResourceLocation BOOK = new ResourceLocation("goety", "black_book");
    private static final String ROOT = "goetyominous";

    private static BookContents cachedContents;
    private static int[] cachedOrder = new int[0];

    public static int[] order() {
        Book book = BookRegistry.INSTANCE.books.get(BOOK);
        if (book == null) {
            return new int[0];
        }
        BookContents contents = book.getContents();
        if (contents != cachedContents) {
            cachedContents = contents;
            cachedOrder = build(contents);
        }
        return cachedOrder;
    }

    private static int[] build(BookContents contents) {
        BookCategory root = root(contents);
        if (root == null) {
            return new int[0];
        }
        List<BookCategory> children = new ArrayList<>(contents.categories.values());
        children.removeIf(category -> category.getParentCategory() != root || category.shouldHide());
        Collections.sort(children);
        int[] buffer = new int[children.size()];
        int count = 0;
        for (BookCategory child : children) {
            int index = OminousIconRotation.indexOf(child.getId().getPath());
            if (index >= 0) {
                buffer[count++] = index;
            }
        }
        return Arrays.copyOf(buffer, count);
    }

    private static BookCategory root(BookContents contents) {
        for (BookCategory category : contents.categories.values()) {
            if (category.isRootCategory() && category.getId().getPath().equals(ROOT)) {
                return category;
            }
        }
        return null;
    }
}
