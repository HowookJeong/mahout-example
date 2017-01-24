package org.henry.proto.mahout;

import java.util.Collection;
import java.util.List;
import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.TopItems;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.Rescorer;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.LongPair;

/**
 * Created by jeonghoug on 2017. 1. 23..
 */
public class ExampleRecommender implements UserBasedRecommender {

  private final UserSimilarity similarity;
  private final Recommender recommender;

  public ExampleRecommender(DataModel dataModel) throws TasteException {
    similarity = new PearsonCorrelationSimilarity(dataModel);

    // 이웃한 N명의 사용자 데이터로 추천 데이터 생성
    // UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, similarity, dataModel, 0.2);

    // 특정 값이나 임계치를 넘는 모든 사용자의 데이터로 추천 데이터 생성, samplingrate : user sampling rate 10%
    // UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, dataModel, 0.1);

//    UserNeighborhood neighborhood = new NearestNUserNeighborhood(10, 0.2, similarity, dataModel, 0.2);
    UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.2, similarity, dataModel, 1.0);
    recommender = new GenericUserBasedRecommender(dataModel, neighborhood, similarity);
  }

  @Override
  public List<RecommendedItem> recommend(long userID, int howMany) throws TasteException {
    return recommender.recommend(userID, howMany);
  }

  @Override
  public List<RecommendedItem> recommend(long userID, int howMany, boolean includeKnownItems) throws TasteException {
    return recommend(userID, howMany, null, includeKnownItems);
  }

  @Override
  public List<RecommendedItem> recommend(long userID, int howMany, IDRescorer rescorer) throws TasteException {
    return recommender.recommend(userID, howMany, rescorer, false);
  }

  @Override
  public List<RecommendedItem> recommend(long userID, int howMany, IDRescorer rescorer, boolean includeKnownItems)
    throws TasteException {
    return recommender.recommend(userID, howMany, rescorer, false);
  }

  @Override
  public float estimatePreference(long userID, long itemID) throws TasteException {
    return recommender.estimatePreference(userID, itemID);
  }

  @Override
  public void setPreference(long userID, long itemID, float value) throws TasteException {
    recommender.setPreference(userID, itemID, value);
  }

  @Override
  public void removePreference(long userID, long itemID) throws TasteException {
    recommender.removePreference(userID, itemID);
  }

  @Override
  public DataModel getDataModel() {
    return recommender.getDataModel();
  }

  @Override
  public void refresh(Collection<Refreshable> alreadyRefreshed) {
    recommender.refresh(alreadyRefreshed);
  }

  @Override
  public String toString() {
    return "MemeProductRecommender[recommender:" + recommender + ']';
  }

  @Override
  public long[] mostSimilarUserIDs(long userID, int howMany) throws TasteException {
    return mostSimilarUserIDs(userID, howMany, null);
  }

  @Override
  public long[] mostSimilarUserIDs(long userID, int howMany, Rescorer<LongPair> rescorer) throws TasteException {
    TopItems.Estimator<Long> estimator = new MostSimilarEstimator(userID, similarity, rescorer);
    return doMostSimilarUsers(howMany, estimator);
  }

  private long[] doMostSimilarUsers(int howMany, TopItems.Estimator<Long> estimator) throws TasteException {
    DataModel model = getDataModel();
    return TopItems.getTopUsers(howMany, model.getUserIDs(), null, estimator);
  }

  private static final class MostSimilarEstimator implements TopItems.Estimator<Long> {

    private final long toUserID;
    private final UserSimilarity similarity;
    private final Rescorer<LongPair> rescorer;

    private MostSimilarEstimator(long toUserID, UserSimilarity similarity, Rescorer<LongPair> rescorer) {
      this.toUserID = toUserID;
      this.similarity = similarity;
      this.rescorer = rescorer;
    }

    @Override
    public double estimate(Long userID) throws TasteException {
      // Don't consider the user itself as a possible most similar user
      if (userID == toUserID) {
        return Double.NaN;
      }
      if (rescorer == null) {
        return similarity.userSimilarity(toUserID, userID);
      } else {
        LongPair pair = new LongPair(toUserID, userID);
        if (rescorer.isFiltered(pair)) {
          return Double.NaN;
        }
        double originalEstimate = similarity.userSimilarity(toUserID, userID);
        return rescorer.rescore(pair, originalEstimate);
      }
    }
  }
}
