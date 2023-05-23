package sg.edu.nus.iss.workshop37.services;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import sg.edu.nus.iss.workshop37.repositories.ArticleRepository;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    public int insertArticleImage(MultipartFile image) {
        try {
            return articleRepository.insertArticleImage(image.getOriginalFilename(), image.getContentType(),
                    image.getBytes());
        } catch (DataAccessException | IOException ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public int insertArticleContent(int id, String title, String content) {
        return articleRepository.insertArticleContent(id, title, content);
    }

    @Transactional(rollbackFor = Exception.class)
    public int postArticle(String title, String content, MultipartFile image) {
        // MySQL
        int imgId = insertArticleImage(image);

        // mongoDB
        int postId = insertArticleContent(imgId, title, content);
        return postId;
    }

    public byte[] getArticleImage(int imageId) {
        try {
            if (articleRepository.getArticleImage(imageId).isEmpty()) {
                return null;
            }
            System.out.println(articleRepository.getArticleImage(imageId).get().filename());
            return articleRepository.getArticleImage(imageId).get().content();
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
