package com.example.blob;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.dataset.ReplacementDataSetLoader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;

@DbUnitConfiguration(dataSetLoader = ReplacementDataSetLoader.class)
@Import(DbUnitConfig.class)
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class
})
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RunWith(SpringRunner.class)
public class FileRepositoryTest {
    @Autowired
    private FileRepository target;

    @Test
    @DatabaseSetup("setup-file1.xml")
    @ExpectedDatabase("expected-file1.xml")
    public void ファイルが保存できることを確認する() throws IOException, URISyntaxException {
        Path file = Paths.get(this.getClass().getResource("/com/example/blob/test.txt").toURI());

        target.save(File.builder()
                .id("1")
                .name(file.getFileName().toString())
                .in(Files.newInputStream(file))
                .build()
        );
    }

    @Test
    @DatabaseSetup("setup-file3.xml")
    public void ファイルが検索できることを確認する() throws IOException {
        File file = target.findOne("1");
        Assert.assertThat(file.getId(), is("1"));
        Assert.assertThat(file.getName(), is("test.txt"));
        Assert.assertThat(convert(file.getIn()), is("test"));
    }

    private static String convert(InputStream in) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    @Test
    @DatabaseSetup("setup-file4.xml")
    @ExpectedDatabase("expected-file2.xml")
    public void 削除する() {
        int actual = target.delete("1");
        Assert.assertThat(actual, is(1));
    }

    @Test
    @DatabaseSetup("setup-file2.xml")
    public void nullに置換できることを確認する() {
        File file = target.findOne("1");
        Assert.assertThat(file.getId(), is("1"));
        Assert.assertThat(file.getName(), nullValue());
    }

    @Test
    @Rollback(false)
    public void 沢山インサートする() throws InterruptedException {
        IntStream.range(1, 5)
                .forEach(i -> {
                    try {
                        Path file = Paths.get(this.getClass().getResource("/com/example/blob/large.zip").toURI());
                        target.save(File.builder()
                                .id(String.valueOf(i))
                                .name("large.zip")
                                .in(Files.newInputStream(file))
                                .build()
                        );
                    } catch (URISyntaxException | IOException e) {
                        e.printStackTrace();
                    }
                });
    }

}