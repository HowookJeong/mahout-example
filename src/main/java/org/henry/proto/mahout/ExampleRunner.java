package org.henry.proto.mahout;

import java.io.File;
import java.util.List;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jeonghoug on 2017. 1. 23..
 */
public class ExampleRunner {
  private static final Logger LOG = LoggerFactory.getLogger(ExampleRunner.class);

  public static void main(String[] args) {

    try {
      ExampleRunner runner = new ExampleRunner();
      Recommender recommender = runner.buildRecommender();

      // 2번 유저에 대한 추천 아이템 3개
      List<RecommendedItem> recommendations = recommender.recommend(710039, 3);

      for (RecommendedItem recommendation : recommendations) {
        LOG.debug("추천 아이템 : {}", recommendation);
      }

    } catch (Exception e) {

    } finally {

    }
  }

  public UserBasedRecommender buildRecommender() throws Exception {
    DataModel dataModel = new FileDataModel(new File("/git/mahout-example/data/user-to-item.csv"));
    return new ExampleRecommender(dataModel);
  }

}
