package in.wynk.utils.dao;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
@Deprecated
public interface IBaseDao<T, ID> extends Repository<T, ID> {

    <S extends T> S save(S save);

    Optional<T> findById(ID id);

    Optional<List<T>> findAll();

}
