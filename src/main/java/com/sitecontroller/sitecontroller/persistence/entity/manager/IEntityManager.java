package com.sitecontroller.sitecontroller.persistence.entity.manager;

import java.util.List;

import com.sitecontroller.sitecontroller.persistence.Filter;
import com.sitecontroller.sitecontroller.persistence.entity.IEntity;

/* 
import com.rft.pinpoint.persistence.CountGroupByResult;

import com.rft.pinpoint.persistence.GroupBy;
import com.rft.pinpoint.persistence.MinGroupByResult;
import com.rft.pinpoint.persistence.MinProperty;
import com.rft.pinpoint.persistence.MaxGroupByResult;
import com.rft.pinpoint.persistence.MaxProperty;
import com.rft.pinpoint.persistence.PagingParameter;
import com.rft.pinpoint.persistence.PagingResult;
import com.rft.pinpoint.persistence.SortParameter;
import com.rft.pinpoint.persistence.SumGroupByResult;
import com.rft.pinpoint.persistence.SumProperty;
import com.rft.pinpoint.persistence.entity.IEntity;
import com.rft.pinpoint.persistence.entity.IEntityMetadata;
import com.rft.pinpoint.persistence.entity.query.IQuery;
*/

public interface IEntityManager<E extends IEntity> {

    List<E> findAll(List<String> relatedEnds);
    E findByFilter1(Filter filter, List<String> relatedEnds);

    /* TO DO: This is a new interface, migrated from Help Alert. 
    The remainder of these will need to be updated to match the new persistence framework. 

    void setEntityManagerContext(IEntityManagerContext entityManagerContext);
    IEntityMetadata<E> getEntityMetadata();
    List<IEntitySaveMonitor<E>> getEntitySaveMonitors();
    void addEntitySaveMonitor(IEntitySaveMonitor<E> enitySaveMonitor);
    void removeEntitySaveMonitor(IEntitySaveMonitor<E> enitySaveMonitor);
    List<IEntityDeleteMonitor<E>> getEntityDeleteMonitors();
    void addEntityDeleteMonitor(IEntityDeleteMonitor<E> enityDeleteMonitor);
    void removeEntityDeleteMonitor(IEntityDeleteMonitor<E> enityDeleteMonitor);
    E newEntityInstance();
    
    E findById(String id, List<String> relatedEnds);
    E findById(String id, List<String> relatedEnds, boolean loadSoftDeletedEntity);
    E findByQuery1(IQuery query, Object[] filterValues, List<String> relatedEnds);
    List<E> findByFilter(Filter filter, List<String> relatedEnds);
    E findByFilter1(Filter filter, List<String> relatedEnds);
    List<E> findByFilters(List<Filter> filters, List<String> relatedEnds);
    E findByFilters1(List<Filter> filters, List<String> relatedEnds);
    List<E> find(List<Filter> filters, List<SortParameter> sortParameters, PagingParameter pagingParameter, PagingResult pagingResult, List<String> relatedEnds);
    int count(List<String> relatedEnds);
    int countByFilter(Filter filter, List<String> relatedEnds);
    int countByFilters(List<Filter> filters, List<String> relatedEnds);
    CountGroupByResult[] countGroup(List<GroupBy> groupBys, List<String> relatedEnds);
    CountGroupByResult[] countGroupByFilter(List<GroupBy> groupBys, Filter filter, List<String> relatedEnds);
    CountGroupByResult[] countGroupByFilters(List<GroupBy> groupBys, List<Filter> filters, List<String> relatedEnds);
    CountGroupByResult[] countGroup(List<GroupBy> groupBys, List<Filter> filters, List<SortParameter> sortParameters, List<String> relatedEnds);
    double sum(SumProperty property, List<String> relatedEnds);
    double sumByFilter(SumProperty property, Filter filter, List<String> relatedEnds);
    double sumByFilters(SumProperty property, List<Filter> filters, List<String> relatedEnds);
    SumGroupByResult[] sumGroup(SumProperty property, List<GroupBy> groupBys, List<String> relatedEnds);
    SumGroupByResult[] sumGroupByFilter(SumProperty property, List<GroupBy> groupBys, Filter filter, List<String> relatedEnds);
    SumGroupByResult[] sumGroupByFilters(SumProperty property, List<GroupBy> groupBys, List<Filter> filters, List<String> relatedEnds);
    SumGroupByResult[] sumGroup(SumProperty property, List<GroupBy> groupBys, List<Filter> filters, List<SortParameter> sortParameters, List<String> relatedEnds);
    double min(MinProperty property, List<String> relatedEnds);
    double minByFilter(MinProperty property, Filter filter, List<String> relatedEnds);
    double minByFilters(MinProperty property, List<Filter> filters, List<String> relatedEnds);
    MinGroupByResult[] minGroup(MinProperty property, List<GroupBy> groupBys, List<String> relatedEnds);
    MinGroupByResult[] minGroupByFilter(MinProperty property, List<GroupBy> groupBys, Filter filter, List<String> relatedEnds);
    MinGroupByResult[] minGroupByFilters(MinProperty property, List<GroupBy> groupBys, List<Filter> filters, List<String> relatedEnds);
    MinGroupByResult[] minGroup(MinProperty property, List<GroupBy> groupBys, List<Filter> filters, List<SortParameter> sortParameters, List<String> relatedEnds);
    double max(MaxProperty property, List<String> relatedEnds);
    double maxByFilter(MaxProperty property, Filter filter, List<String> relatedEnds);
    double maxByFilters(MaxProperty property, List<Filter> filters, List<String> relatedEnds);
    MaxGroupByResult[] maxGroup(MaxProperty property, List<GroupBy> groupBys, List<String> relatedEnds);
    MaxGroupByResult[] maxGroupByFilter(MaxProperty property, List<GroupBy> groupBys, Filter filter, List<String> relatedEnds);
    MaxGroupByResult[] maxGroupByFilters(MaxProperty property, List<GroupBy> groupBys, List<Filter> filters, List<String> relatedEnds);
    MaxGroupByResult[] maxGroup(MaxProperty property, List<GroupBy> groupBys, List<Filter> filters, List<SortParameter> sortParameters, List<String> relatedEnds);
    void lock(String id);
    void save(List<E> entities);
    void save(E entity);
    E saveCopy(E entity, List<String> copyRelatedEnds);
    List<E> saveCopy(List<E> entities, List<String> copyRelatedEnds);
    void delete(E entity);
    void deleteById(String id);
    int deleteByFilter(Filter filter, List<String> relatedEnds);
    int deleteByFilters(List<Filter> filter, List<String> relatedEnds);
    int deleteAll();
    int exportAll(IEntityExporter<E> entityExporter);
    void import1(E entity);
    */
}
