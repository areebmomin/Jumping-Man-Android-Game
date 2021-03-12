package com.areeb.jumpingman;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class JumpingMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	int manState = 0;
	int pause = 0;
    float gravity = 0.2f;
    float velocity = 0;
    int manY = 0;
	Rectangle manRectangle;
	long score = 0, previousScore = -1;
	BitmapFont bitmapFont;
	int gameState = 0;
	Texture dizzyMan;

	ArrayList<Integer> coinXs = new ArrayList<>();
	ArrayList<Integer> coinYs = new ArrayList<>();
	ArrayList<Rectangle> coinRectangles = new ArrayList<>();
	Texture coin;
	int coinCount;

	ArrayList<Integer> bombXs = new ArrayList<>();
	ArrayList<Integer> bombYs = new ArrayList<>();
	ArrayList<Rectangle> bombRectangles = new ArrayList<>();
	Texture bomb;
	int bombCount;

	Random random;

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");

		manY = Gdx.graphics.getHeight() / 2;

		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		dizzyMan = new Texture("dizzy-1.png");

		random = new Random();

		bitmapFont = new BitmapFont(Gdx.files.internal("font.fnt"),
				Gdx.files.internal("font.png"), false);
		bitmapFont.setColor(Color.WHITE);
		bitmapFont.getData().setScale(2);
	}

	public void makeCoin() {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		coinYs.add((int) height);
		coinXs.add(Gdx.graphics.getWidth());
	}

	public void makeBomb() {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bombYs.add((int) height);
		bombXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();

		//for background
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState == 1) {
			//game is live

			//for bombs
			if (bombCount < 250) {
				bombCount++;
			} else {
				bombCount = 0;
				makeBomb();
			}

			bombRectangles.clear();
			for (int i = 0; i < bombXs.size(); i++) {
				batch.draw(bomb, bombXs.get(i), bombYs.get(i));
				bombXs.set(i, bombXs.get(i) - 8);
				bombRectangles.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
			}

			//for coins
			if (coinCount < 100) {
				coinCount++;
			} else {
				coinCount = 0;
				makeCoin();
			}

			coinRectangles.clear();
			for (int i = 0; i < coinXs.size(); i++) {
				batch.draw(coin, coinXs.get(i), coinYs.get(i));
				coinXs.set(i, coinXs.get(i) - 4);
				coinRectangles.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
			}

			if (Gdx.input.justTouched()) {
				velocity = -10;
			}

			//for man
			if (pause < 8) {
				pause++;
			} else {
				pause = 0;
				if (manState < 3) {
					manState++;
				} else {
					manState = 0;
				}
			}

			velocity += gravity;
			manY -= velocity;

			if (manY <= 0) {
				manY = 0;
			} else if (manY >= Gdx.graphics.getHeight()) {
				manY = Gdx.graphics.getHeight();
			}

		} else if (gameState == 0) {
			//waiting to start
			if (Gdx.input.justTouched()) {
				gameState = 1;
			}
		} else if (gameState == 2) {
			//game over
			if (Gdx.input.justTouched()) {
				gameState = 1;
				manY = Gdx.graphics.getHeight() / 2;
				score = 0;
				previousScore = -1;
				velocity = 0;
				coinXs.clear();
				coinYs.clear();
				coinRectangles.clear();
				coinCount = 0;
				bombXs.clear();
				bombYs.clear();
				bombRectangles.clear();
				bombCount = 0;
			}
		}

		//draw man
		if (gameState == 2) {
			 batch.draw(dizzyMan, (float) Gdx.graphics.getWidth() / 2 - (float) man[manState].getWidth(), manY,
					 (float) man[manState].getWidth() / 2, (float) man[manState].getHeight() / 2);
		} else {
			batch.draw(man[manState], (float) Gdx.graphics.getWidth() / 2 - (float) man[manState].getWidth(), manY,
					(float) man[manState].getWidth() / 2, (float) man[manState].getHeight() / 2);
		}

        manRectangle = new Rectangle((int) (Gdx.graphics.getWidth() / 2 - (float) man[manState].getWidth()), manY,
				(float) man[manState].getWidth() / 2, (float) man[manState].getHeight() / 2);

        for (int i = 0; i < coinRectangles.size(); i++) {
        	if (Intersector.overlaps(manRectangle, coinRectangles.get(i))) {
        		previousScore = score;
        		score++;

        		coinRectangles.remove(i);
        		coinXs.remove(i);
        		coinYs.remove(i);
        		break;
			}
		}

		for (int i = 0; i < bombRectangles.size(); i++) {
			if (Intersector.overlaps(manRectangle, bombRectangles.get(i))) {
				gameState = 2;
			}
		}

		//show score
		if (previousScore != score)
			bitmapFont.draw(batch, String.valueOf(score), 100, 200);

		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}
