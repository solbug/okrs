package com.okr.utils;


import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonService.class);

    @Autowired
    private EntityManager entityManager;

    public Session getSession() {
        return entityManager.unwrap(Session.class);
    }

    public SQLQuery createSQLQuery(String sql) {
        return getSession().createSQLQuery(sql);
    }

    public void setResultTransformer(SQLQuery query, Class obj) {
        Field[] fileds = obj.getDeclaredFields();
        Map<String, String> mapFileds = new HashMap();
        for (Field filed : fileds) {
            mapFileds.put(filed.getName(), filed.getGenericType().toString());
        }
        List<String> aliasColumns = getReturnAliasColumns(query);
        for (String aliasColumn : aliasColumns) {
            String dataType = mapFileds.get(aliasColumn);
            if (dataType != null) {
                Type hbmType = null;
                if ("class java.lang.Long".equals(dataType)) {
                    hbmType = LongType.INSTANCE;
                } else if ("class java.lang.Integer".equals(dataType)) {
                    hbmType = IntegerType.INSTANCE;
                } else if ("class java.lang.Double".equals(dataType)) {
                    hbmType = DoubleType.INSTANCE;
                } else if ("class java.lang.String".equals(dataType)) {
                    hbmType = StringType.INSTANCE;
                } else if ("class java.lang.Boolean".equals(dataType)) {
                    hbmType = BooleanType.INSTANCE;
                } else if ("class java.util.Date".equals(dataType)) {
                    hbmType = TimestampType.INSTANCE;
                } else if ("class java.math.BigDecimal".equals(dataType)) {
                    hbmType = new BigDecimalType();
                }

                if (hbmType != null) {
                    query.addScalar(aliasColumn, hbmType);
                }
            }
        }
        query.setResultTransformer(Transformers.aliasToBean(obj));
    }

    public List<String> getReturnAliasColumns(SQLQuery query) {
        List<String> aliasColumns = new ArrayList();
        String sqlQuery = query.getQueryString();
        sqlQuery = sqlQuery.replace("\n", " ");
        sqlQuery = sqlQuery.replace("\t", " ");
        int numOfRightPythis = 0;
        int startPythis = -1;
        int endPythis = 0;
        boolean hasRightPythis = true;
        while (hasRightPythis) {
            char[] arrStr = sqlQuery.toCharArray();
            hasRightPythis = false;
            int idx = 0;
            for (char c : arrStr) {
                if (idx > startPythis) {
                    if ("(".equalsIgnoreCase(String.valueOf(c))) {
                        if (numOfRightPythis == 0) {
                            startPythis = idx;
                        }
                        numOfRightPythis++;
                    } else if (")".equalsIgnoreCase(String.valueOf(c))) {
                        if (numOfRightPythis > 0) {
                            numOfRightPythis--;
                            if (numOfRightPythis == 0) {
                                endPythis = idx;
                                break;
                            }
                        }
                    }
                }
                idx++;
            }
            if (endPythis > 0) {
                sqlQuery = sqlQuery.substring(0, startPythis) + " # " + sqlQuery.substring(endPythis + 1);
                hasRightPythis = true;
                endPythis = 0;
            }
        }
        String arrStr[] = sqlQuery.substring(0, sqlQuery.toUpperCase().indexOf(" FROM ")).split(",");
        for (String str : arrStr) {
            String[] temp = str.trim().split(" ");
            String alias = temp[temp.length - 1].trim();
            if (alias.contains(".")) {
                alias = alias.substring(alias.lastIndexOf(".") + 1).trim();
            }
            if (alias.contains(",")) {
                alias = alias.substring(alias.lastIndexOf(",") + 1).trim();
            }
            if (alias.contains("`")) {
                alias = alias.replace("`", "");
            }
            if (!aliasColumns.contains(alias)) {
                aliasColumns.add(alias);
            }
        }
        return aliasColumns;
    }

    public <T> DataTableResults<T> findPaginationQueryCustom(String nativeQuery, String orderBy
            , List<Object> paramList, Class obj, Integer pageSize, Integer pageNumber) {
        return findPaginationCustom(nativeQuery, orderBy, paramList, obj,pageNumber, pageSize);
    }

    private <T> DataTableResults<T> findPaginationCustom(String nativeQuery, String orderBy, List<Object> paramList
            , Class obj, Integer pageSize, Integer pageNumber) {
        if (Mixin.NVL(pageSize) == 0L) {
            pageSize = 10;
        }
        if (Mixin.NVL(pageNumber) == 0L) {
            pageNumber = 1;
        }

        Date _date = new Date();
        SearchParams searchParams = new SearchParams();
        searchParams.setRows(pageSize);
        searchParams.setFirst(pageSize * (pageNumber - 1));
        searchParams.setSortOrder(1);

        String paginatedQuery = buildPaginatedQuery(nativeQuery, orderBy, searchParams);
        String countStrQuery = buildCountQuery(nativeQuery);
        LOGGER.info("paginatedQuery: " + paginatedQuery);
        LOGGER.info("countStrQuery: " + countStrQuery);
        SQLQuery query = createSQLQuery(paginatedQuery);
        setResultTransformer(query, obj);
        // pagination
        query.setFirstResult(Mixin.NVL(searchParams.getFirst()));
        query.setMaxResults(Mixin.NVL(searchParams.getRows(), pageSize.intValue()));
        SQLQuery countQuery = createSQLQuery(countStrQuery);

        if (!Mixin.isNullOrEmpty(paramList)) {
            int paramSize = paramList.size();
            for (int i = 0; i < paramSize; i++) {
                countQuery.setParameter(i +1 , paramList.get(i));
                query.setParameter(i + 1, paramList.get(i));
            }
        }

        List<T> userList = query.list();
        Object totalRecords = countQuery.uniqueResult();

        DataTableResults<T> dataTableResult = new DataTableResults<T>();
        dataTableResult.setData(userList);
        if (!Mixin.isEmpty(userList)) {
            dataTableResult.setRecordsTotal(String.valueOf(totalRecords));
            dataTableResult.setRecordsFiltered(String.valueOf(totalRecords));
            dataTableResult.setFirst(String.valueOf(Mixin.NVL(searchParams.getFirst())));
        } else {
            dataTableResult.setRecordsFiltered("0");
            dataTableResult.setRecordsTotal("0");
        }

        LOGGER.info("time query: " + (new Date().getTime() - _date.getTime()));
        return dataTableResult;
    }

    public static String buildPaginatedQuery(String baseQuery, String orderBy, SearchParams searchParams) {
        if (!Mixin.isEmpty(searchParams)) {
            if (!"".equals(Mixin.NVL(searchParams.getOrderByClause()))) {
                orderBy = searchParams.getOrderByClause();
            }
        }
        StringBuilder sb = new StringBuilder("#BASE_QUERY# #ORDER_CLASUE# ");
        String finalQuery = sb.toString().replaceAll("#BASE_QUERY#", baseQuery).replaceAll("#ORDER_CLASUE#",
                Mixin.NVL(orderBy));
        return finalQuery;
    }

    public static String buildCountQuery(String baseQuery) {
        StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM (#BASE_QUERY#) FILTERED_ORDERD_RESULTS ");
        String finalQuery = null;
        finalQuery = sb.toString().replaceAll("#BASE_QUERY#", baseQuery);
        return (null == finalQuery) ? baseQuery : finalQuery;
    }

    /**
     *
     * @param nativeQuery
     * @param paramList
     * @param obj
     * @param <T>
     * @return
     */
    public <T> List<T> list(String nativeQuery, List<Object> paramList, Class obj) {
        SQLQuery query = createSQLQuery(nativeQuery);
        setResultTransformer(query, obj);

        if (!Mixin.isNullOrEmpty(paramList)) {
            int paramSize = paramList.size();
            for (int i = 0; i < paramSize; i++) {
                query.setParameter(i, paramList.get(i));
            }
        }
        return query.list();
    }

     /**
     *
     * @param nativeQuery
     * @param mapParams
     * @param obj
     * @param <T>
     * @return
     */
    public <T> List<T> list(String nativeQuery, Map<String, Object> mapParams, Class obj) {
        SQLQuery query = createSQLQuery(nativeQuery);
        setResultTransformer(query, obj);

        if (mapParams != null && !mapParams.isEmpty()) {
            query.setProperties(mapParams);
        }
        return query.list();
    }
}
