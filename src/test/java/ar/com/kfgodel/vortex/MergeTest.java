package ar.com.kfgodel.vortex;

import ar.com.dgarcia.javaspec.api.JavaSpec;
import ar.com.dgarcia.javaspec.api.JavaSpecRunner;
import ar.com.dgarcia.javaspec.api.TestContext;
import ar.com.kfgodel.iterables.Collections;
import ar.com.kfgodel.iterables.MergeResult;
import org.junit.runner.RunWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This type verifies the correct behavior of the merge result calculation
 * Created by ikari on 21/01/2015.
 */
@RunWith(JavaSpecRunner.class)
public class MergeTest extends JavaSpec<TestContext> {
    @Override
    public void define() {
        describe("a merge result", () -> {

            it("can be created from two collections",()->{
                MergeResult<Integer> mergeResult = Collections.merge(Collections.newList(1, 2, 3), Collections.newList(1, 2, 3));
                assertThat(mergeResult).isNotNull();
            });

            describe("algorithm", () -> {

                it("extracts as added elements only present in the newer collection",()->{
                    List<Integer> older = Collections.newList(1, 2, 3, 4);
                    List<Integer> newer = Collections.newList(3, 4, 5, 6);

                    MergeResult<Integer> mergeResult = Collections.merge(older,newer);

                    assertThat(mergeResult.getAdded()).isEqualTo(Collections.newOrderedSet(5, 6));
                });
                it("extracts as removed elements only present in the older collection",()->{
                    List<Integer> older = Collections.newList(1, 2, 3, 4);
                    List<Integer> newer = Collections.newList(3, 4, 5, 6);

                    MergeResult<Integer> mergeResult = Collections.merge(older,newer);

                    assertThat(mergeResult.getRemoved()).isEqualTo(Collections.newOrderedSet(1, 2));
                });
                it("extracts as kept elements present in both collection",()->{
                    List<Integer> older = Collections.newList(1, 2, 3, 4);
                    List<Integer> newer = Collections.newList(3, 4, 5, 6);

                    MergeResult<Integer> mergeResult = Collections.merge(older,newer);

                    assertThat(mergeResult.getKept()).isEqualTo(Collections.newOrderedSet(3, 4));
                });
                
                it("extracts all newer as added if older is empty",()->{
                    List<Integer> older = Collections.newList();
                    List<Integer> newer = Collections.newList(3, 4, 5, 6);

                    MergeResult<Integer> mergeResult = Collections.merge(older,newer);

                    assertThat(mergeResult.getAdded()).isEqualTo(Collections.newOrderedSet(3, 4, 5, 6));
                }); 
                
                it("extracts all older as removed if newer is empty",()->{
                    List<Integer> older = Collections.newList(1, 2, 3, 4);
                    List<Integer> newer = Collections.newList();

                    MergeResult<Integer> mergeResult = Collections.merge(older,newer);

                    assertThat(mergeResult.getRemoved()).isEqualTo(Collections.newOrderedSet(1, 2, 3, 4));
                });   
                
                it("extracts all as kept is collection are equals",()->{
                    List<Integer> older = Collections.newList(1, 2, 3, 4);
                    List<Integer> newer = Collections.newList(1, 2, 3, 4);

                    MergeResult<Integer> mergeResult = Collections.merge(older,newer);

                    assertThat(mergeResult.getKept()).isEqualTo(Collections.newOrderedSet(1, 2, 3, 4));
                }); 
                
                it("extracts all as kept if collection contains same elements disregarding order",()->{
                    List<Integer> older = Collections.newList(1, 2, 3, 4);
                    List<Integer> newer = Collections.newList(3, 2, 4, 1);

                    MergeResult<Integer> mergeResult = Collections.merge(older,newer);

                    assertThat(mergeResult.getKept()).isEqualTo(Collections.newOrderedSet(1, 2, 3, 4));
                });   

            });
        });

    }
}