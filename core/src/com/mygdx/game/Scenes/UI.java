package com.mygdx.game.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MarioBros;

public class UI implements Disposable {
    public Stage stage;
    private Viewport viewport;

    private Integer worldTimer;
    private float timeCount;
    private static Integer score;
    private static Integer lives;

    private boolean timeUp;

    private Label countdownLabel;
    private static Label scoreLabel;
    private static  Label lifeCount;
    private Label levelLabel;
    private Label worldLabel;
    private Label timeLabel;
    private Label marioLabel;
    private Label livesLabel;

    public UI(SpriteBatch sb){
        worldTimer = 300;
        timeCount = 0;
        score = 0;
        lives = 3;

        viewport = new FitViewport(MarioBros.V_WIDTH,MarioBros.V_HEIGHT,new OrthographicCamera());
        stage = new Stage(viewport,sb);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        countdownLabel = new Label(String.format("%03d",worldTimer),new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel =  new Label(String.format("%06d",score),new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        lifeCount = new Label(String.format("%01d",lives),new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        timeLabel = new Label("TIME",new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        levelLabel = new Label("1-1",new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        worldLabel = new Label("WORLD",new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        marioLabel = new Label("MARIO",new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        livesLabel = new Label("LIVES",new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        table.add(marioLabel).expandX().padTop(10);
        table.add(worldLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.add(livesLabel).expandX().padTop(10);
        table.row();
        table.add(scoreLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(countdownLabel).expandX();
        table.add(lifeCount).expandX();


        stage.addActor(table);


    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public void update(float dt){
        timeCount += dt;
        if(timeCount >= 1) {
            if (worldTimer > 0) {
                worldTimer--;
            } else {
                timeUp = true;
            }

            countdownLabel.setText(String.format("%03d", worldTimer));
            timeCount = 0;
        }
    }

    public static void addScore(int value){
        score += value;
        scoreLabel.setText(String.format("%06d",score));
    }

    public boolean isTimeUp() {
        return timeUp;
    }

    public static void loseLife(){
        lives -= 1;
        lifeCount.setText(String.format("%01d",lives));
    }

    public static Integer getLives(){
        return lives;
    }
}
