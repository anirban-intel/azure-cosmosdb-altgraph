package org.cjoakim.cosmos.altgraph.data.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.azure.spring.data.cosmos.repository.Query;
import org.cjoakim.cosmos.altgraph.data.model.Triple;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * Chris Joakim, Microsoft, July 2022
 */

@Component
@Repository
public interface TripleRepository extends CosmosRepository<Triple, String>, TripleRepositoryExtensions {
    Iterable<Triple> findBySubjectType(String subjectType);
    Iterable<Triple> findBySubjectLabel(String subjectLabel);
    Iterable<Triple> findByTenantAndSubjectLabel(String tenant, String subjectLabel);
    @Query("select value count(1) from c")
    long countAllTriples();
    @Query("select value count(1) from c where c.subjectLabel = @subjectLabel")
    long getNumberOfDocsWithSubjectLabel(@Param("subjectLabel") String subjectLabel);
    @Query("select * from c where c.pk = @pk and c.lob = @lob and c.subjectType = @subjectType and c.objectType = @objectType")
    List<Triple> getByPkLobAndSubjects(
            @Param("pk") String pk,      // "pk": "triple|123"
            @Param("lob") String lob,
            @Param("subjectType") String subjectType,
            @Param("objectType") String objectType);

    // See the following Composite Index in altgraph.json
    // [
    //     {
    //         "path": "/pk",
    //             "order": "ascending"
    //     },
    //     {
    //         "path": "/lob",
    //             "order": "ascending"
    //     },
    //     {
    //         "path": "/subjectType",
    //             "order": "ascending"
    //     },
    //     {
    //         "path": "/objectType",
    //             "order": "ascending"
    //     }
    // ]

    @Query("select * from c where c.tenant = @tenant and ARRAY_CONTAINS(@subjects, c.subjectLabel)")
    List<Triple> getByTenantAndSubjectLabels(
            @Param("tenant") String tenant,
            @Param("subjects") List<String> subjectLabels);
}