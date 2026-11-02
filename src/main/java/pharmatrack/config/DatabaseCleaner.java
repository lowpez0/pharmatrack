package pharmatrack.config;


import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCleaner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseCleaner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");

        jdbcTemplate.execute("TRUNCATE TABLE med_order_item");
        jdbcTemplate.execute("TRUNCATE TABLE med_order");
        jdbcTemplate.execute("TRUNCATE TABLE transaction_item");
        jdbcTemplate.execute("TRUNCATE TABLE sales_transaction");
        jdbcTemplate.execute("TRUNCATE TABLE batch");
        jdbcTemplate.execute("TRUNCATE TABLE medicine");
        jdbcTemplate.execute("TRUNCATE TABLE `user`");

        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }
}
