package org.henry.proto.mahout;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

/**
 * Created by jeonghoug on 2017. 1. 23..
 */
public class ExampleRecommenderBuilder implements RecommenderBuilder {

  @Override
  public Recommender buildRecommender(DataModel dataModel) throws TasteException {
    return new ExampleRecommender(dataModel);
  }
}
