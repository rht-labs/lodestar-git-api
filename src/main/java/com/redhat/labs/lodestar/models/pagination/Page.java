package com.redhat.labs.lodestar.models.pagination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriBuilder;

import com.redhat.labs.lodestar.models.Engagement;
import com.redhat.labs.lodestar.models.gitlab.Project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Page {

    private static final String PAGE = "page";
    private static final String PER_PAGE = "per_page";
    private static final String PAGE_HEADER_FORMAT = "x-%s-page";
    private static final String PER_PAGE_HEADER = "x-per-page";

    private static final String FIRST = "first";
    private static final String FIRST_PAGE_HEADER = String.format(PAGE_HEADER_FORMAT, FIRST);

    private static final String NEXT = "next";
    private static final String NEXT_PAGE_HEADER = String.format(PAGE_HEADER_FORMAT, NEXT);

    private static final String LAST = "last";
    private static final String LAST_PAGE_HEADER = String.format(PAGE_HEADER_FORMAT, LAST);

    private static final String REGEX = ".*&page=(\\d+)&.*";

    @Builder.Default
    Integer page = 1;

    @Builder.Default
    Integer perPage = 20;
    String gitLabLinkHeader;

    @Builder.Default
    Map<String, Map<String, Integer>> linkHeaders = new HashMap<>();

    @Builder.Default
    Map<String, Object> headers = new HashMap<>();

    @Builder.Default
    List<Project> results = new ArrayList<>();

    @Builder.Default
    List<Engagement> engagements = new ArrayList<>();

    public void setHeadersFromGitLabHeader() {

        if (null == this.gitLabLinkHeader) {
            return;
        }

        // clear before reloading
        linkHeaders.clear();

        setHeadersForRelations();

    }

    private void setHeadersForRelations() {

        // split links
        String[] links = gitLabLinkHeader.split(",");

        headers.put(PER_PAGE_HEADER, perPage);

        for (String link : links) {

            String rel;
            String pageHeader;
            Integer pageValue = Integer.valueOf(link.replaceAll(REGEX, "$1"));

            if (link.contains(FIRST)) {

                rel = FIRST;
                linkHeaders.put(FIRST, new HashMap<>());
                pageHeader = FIRST_PAGE_HEADER;

            } else if (link.contains(NEXT)) {

                rel = NEXT;
                linkHeaders.put(NEXT, new HashMap<>());
                pageHeader = NEXT_PAGE_HEADER;

            } else if (link.contains(LAST)) {

                rel = LAST;
                linkHeaders.put(LAST, new HashMap<>());
                pageHeader = LAST_PAGE_HEADER;

            } else {
                continue;
            }

            headers.put(pageHeader, pageValue);
            linkHeaders.get(rel).put(PAGE, pageValue);
            linkHeaders.get(rel).put(PER_PAGE, perPage);

        }

    }

    public Link[] getLinks(UriBuilder uriBuilder) {

        List<Link> links = linkHeaders.entrySet().stream().map(e1 -> {

            String rel = e1.getKey();
            Map<String, Integer> headerMap = e1.getValue();

            javax.ws.rs.core.Link.Builder builder = Link.fromUriBuilder(uriBuilder).rel(rel);
            headerMap.entrySet().forEach(e2 -> builder.param(e2.getKey(), String.valueOf(e2.getValue())));  

            return builder.build();

        }).collect(Collectors.toList());

        return links.toArray(new Link[links.size()]);

    }

}
