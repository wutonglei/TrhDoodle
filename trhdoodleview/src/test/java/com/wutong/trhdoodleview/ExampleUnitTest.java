package com.wutong.trhdoodleview;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        List<Integer> integers = new ArrayList<>();


        Tree0 tree0 = new Tree0();
        Tree0 tree00;
        Tree1 tree1 = new Tree1();
        Tree1 tree11;

        tree00 = (Tree0) tree0.clone();
        tree11 = (Tree1) tree1.clone();
        tree11.setStr("我改了11");
        tree00.setStr("我改了00");

        tree00.getTree2().x = 100;
        tree11.getTree2().x = 100;


        System.out.println(tree0);
        System.out.println(tree1);


        System.out.println("tree0:" + tree0.hashCode());
        System.out.println("tree00:" + tree00.hashCode());
        System.out.println("tree1:" + tree1.hashCode());
        System.out.println("tree11:" + tree11.hashCode());
        System.out.println("--------以下是tree2的------------");
        System.out.println("tree0:" + tree0.getTree2().hashCode());
        System.out.println("tree00:" + tree00.getTree2().hashCode());
        System.out.println("tree1:" + tree1.getTree2().hashCode());
        System.out.println("tree11:" + tree11.getTree2().hashCode());


//


        for (int i = 0; i < 20; i++) {
            integers.add(i);
            System.out.println(integers.get(i));
        }
        int x = 10;
        for (int i = 0; i < x; x--) {
            integers.remove(i);
        }
        System.out.println(integers);

    }
}