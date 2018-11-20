package mp3.main;

import mp3.dao.implementations.SQLiteDAO;
import mp3.dao.objects.Author;
import mp3.dao.objects.MP3;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

public class Start {

    public static void main(String[] args) {

        Author author = new Author();
        author.setName("Justin");

        MP3 mp3 = new MP3();
        mp3.setName("Song #5");
        mp3.setAuthor(author);


        ApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
        SQLiteDAO sqLiteDAO = (SQLiteDAO) context.getBean("sqliteDAO");
        sqLiteDAO.insertMP3(mp3);

        Map<String, Integer> map = sqLiteDAO.getStat();
        map.forEach((k, v) -> System.out.println(k + " - " + v));

    }

}
