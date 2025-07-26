/**
 *
 */
package com.rlabs.crm.repository.esrepository;

import com.rlabs.crm.RlcrmApp;
import com.rlabs.crm.config.Constants;
import com.rlabs.crm.domain.IGlobalSearch;
import com.rlabs.crm.helper.EsHelper;
import com.rlabs.crm.payload.request.elasticsearch.EsMessageRequest;
import jakarta.persistence.EntityManager;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.io.Serializable;
import java.util.List;

/**
 * @author Manoj Sharma
 */
public class CustomRepository<T, ID extends Serializable>
		extends SimpleJpaRepository<T, ID> implements ICustomElasticSearchRepository<T, ID> {

    private static final Logger logger = LoggerFactory.getLogger(CustomRepository.class);

    private EntityManager entityManager;
    public CustomRepository(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
    }

    public CustomRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    @Override
    public void deleteById(ID id) {
        T entity = super.getOne(id);
        super.deleteById(id);
        fireEvent(Constants.DELETE, entity);
    }

    @Override
    public void delete(T entity) {
        super.delete(entity);
        fireEvent(Constants.DELETE, entity);
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        super.deleteAll(entities);
        if (entities != null) {
            entities.forEach(entity -> {
                fireEvent(Constants.DELETE, entity);
            });
        }
    }

    @Override
    public void deleteInBatch(Iterable<T> entities) {
        super.deleteInBatch(entities);
        if (entities != null) {
            entities.forEach(entity -> {
                fireEvent(Constants.DELETE, entity);
            });
        }
    }

    @Override
    public void deleteAll() {
        List<T> entities = super.findAll();
        super.deleteAll();
        if (entities != null) {
            entities.forEach(entity -> {
                fireEvent(Constants.DELETE, entity);
            });
        }
    }

    @Override
    public void deleteAllInBatch() {
        List<T> entities = super.findAll();
        super.deleteAllInBatch();
        if (entities != null) {
            entities.forEach(entity -> {
                fireEvent(Constants.DELETE, entity);
            });
        }
    }

    @Override
    public <S extends T> S save(S entity) {
        S ent = super.save(entity);
        logger.debug("calling save");
        fireEvent(Constants.SAVE, ent);
        return ent;
    }

//    @Override
//    public <S extends T> S saveAndFlush(S entity) {
//        logger.debug("calling saveAndFlush");
//        S ent = super.saveAndFlush(entity);
//        fireEvent(ent);
//        return ent;
//    }

    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        List<S> entityList = super.saveAll(entities);
        logger.debug("calling save all");
        if (entityList != null) {
            entityList.forEach(entity -> {
                fireEvent(Constants.SAVE, entity);
            });
        }
        return entityList;
    }

    public void fireEvent(String eventType, T entity) {
        if (entity instanceof IGlobalSearch) {
            EntityManager entityManager1 = RlcrmApp.getBean(EntityManager.class);

            JpaEntityInformation entityInformation = JpaEntityInformationSupport.getEntityInformation(entity.getClass(), entityManager1);
            EsHelper esHelper = RlcrmApp.getBean(EsHelper.class);

            if(Constants.SAVE.equalsIgnoreCase(eventType)){
                EsMessageRequest esMessageRequest = EsMessageRequest.builder()
                    .dbId(String.valueOf((Long) entityInformation.getId(entity)))
                    .sourceType(entity.getClass().getName())
                    .sourceData(entity)
                    .build();
                SearchResponse searchResponse = esHelper.searchEsWithDbIdAndClassName(esMessageRequest.getDbId(), esMessageRequest.getSourceType());
                SearchHits searchHits = searchResponse.getHits();
                if(searchHits != null && searchHits.getHits() != null && searchHits.getHits().length > 0){
                    searchHits.forEach(hit -> {
                        esHelper.edit(esMessageRequest, hit.getId());
                    });
                }else{
                    esHelper.add(esMessageRequest);
                }
            }else if(Constants.DELETE.equalsIgnoreCase(eventType)){
                SearchResponse searchResponse = esHelper.searchEsWithDbIdAndClassName(String.valueOf((Long) entityInformation.getId(entity)), entity.getClass().getName());
                searchResponse.getHits().forEach(hit -> {
                    esHelper.delete(hit.getId());
                });
            }

       }
    }

}
