package org.ambraproject.util;

import com.google.common.collect.ImmutableList;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.orm.hibernate3.HibernateTemplate;

import java.util.List;
import java.util.stream.Collectors;

public class Haxx {

  public static <T> List<T> findByCriteria(HibernateTemplate hibernateTemplate, DetachedCriteria criteria) {

    List<Integer> numbers = ImmutableList.of(1, 2, 3, 4, 5);
    List<Object> doubled = numbers.stream()
        .map(n -> n * 2)
        .collect(Collectors.toList());
    System.out.println("!!!!!!!!");
    System.out.println("!!!!!!!!");
    System.out.println("!!!!!!!!");
    System.out.println("!!!!!!!!");
    System.out.println(doubled);
    System.out.println("!!!!!!!!");
    System.out.println("!!!!!!!!");
    System.out.println("!!!!!!!!");
    System.out.println("!!!!!!!!");
    System.out.println("!!!!!!!!");
    System.out.println("!!!!!!!!");

    return (List<T>) hibernateTemplate.findByCriteria(criteria);
  }

  public static <T> List<T> findByCriteria(HibernateTemplate hibernateTemplate, DetachedCriteria criteria, int firstResult, int maxResults) {
    return (List<T>) hibernateTemplate.findByCriteria(criteria, firstResult, maxResults);
  }

}
