package in.wynk.ut.base.batch.writer;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class FilterUserBatchWriter<T> implements ItemWriter<T> {
    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Override
    public void write(List<? extends T> items) throws Exception {
        if (!CollectionUtils.isEmpty(items)) {
            reactiveMongoTemplate.insertAll(items);
        }
    }
}
