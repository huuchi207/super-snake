package com.mywill.supersnake.Magics;


import com.badlogic.gdx.utils.Array;
import com.mywill.supersnake.Colors;
import com.mywill.supersnake.Magic;
import com.mywill.supersnake.Snake;

public class StarPoint extends Magic {
    public StarPoint(float x, float y) {
        super(x, y, "star.png");
    }

    public boolean draw() {
        rotation = drawn % 360;

        return super.draw();
    }

    public void action(Snake snake, Array<Magic> magics) {
        snake.point++;

        if (snake.color != Colors.snake) {
            snake.point++;
        }

        snake.color = Colors.snake;
        snake.addTail();
    }

    public boolean iter() {
        return true;
    }
}
